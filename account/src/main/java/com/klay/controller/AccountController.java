package com.klay.controller;

import com.klay.Api.ApiResult;
import com.klay.entity.User;
import com.klay.mapper.AdminMapper;
import com.klay.mapper.UserMapper;
import com.klay.service.SmsService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: KlayHu
 * @create: 2019/12/18 15:20
 **/

@RestController
@RequestMapping("/account")
@Log
public class AccountController {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SmsService smsService;

    @GetMapping("/login/{username}/{password}/{type}")
    public Object login(@PathVariable("username") String username ,@PathVariable("password") String password,
                        @PathVariable("type")String type){
        Object object = null;
        switch(type){
            case "user":
                return object = userMapper.login(username, password);
            case "admin":
                return object = adminMapper.login(username, password);
        }
        return object;
    }

    /**
     * 验证码发送
     */
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult sendCode(@RequestParam(value = "telephone") String telephone) {
        ApiResult result = new ApiResult();
        String code = redisTemplate.opsForValue().get(telephone);
        if (!StringUtils.isEmpty(code)) {
            //todo 验证码未过期，页面处理
            log.info("===========================验证码还未过期，请等待60s后重新发送===========================");
            result.setCode(ApiResult.SEND_RETRY.getCode());
            result.setMsg(ApiResult.SEND_RETRY.getMsg());
            return result;
        }
        code = String.valueOf(Math.random() * 10000).substring(0, 4);
        HashMap<String, Object> param = new HashMap<>();
        param.put("code", code);
        boolean isSend = smsService.send(telephone, "SMS_205471000", param);
        if (isSend) {
            redisTemplate.opsForValue().set(telephone, code, 1, TimeUnit.MINUTES);
            result.setCode(ApiResult.SEND_SUCCESS.getCode());
            result.setMsg(ApiResult.SEND_SUCCESS.getMsg());
            log.info("==============手机号：" + telephone + " 验证码：" + code + " 发送成功！===============");
            //session.setAttribute("code", code);
            return result;
        }
        log.info("==========================验证码发送失败=========================");
        result.setCode(ApiResult.SEND_FAIL.getCode());
        result.setMsg(ApiResult.SEND_FAIL.getMsg());
        return result;
    }

    /**
     * 验证码校验
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult verifyCode(@RequestParam(value = "code") String code, @RequestParam("username") String username,
                                @RequestParam("password")String password,@RequestParam("telephone")String telephone) {
        ApiResult result = new ApiResult();
        if (StringUtils.isEmpty(redisTemplate.opsForValue().get(telephone))) {
            return null;
        }
        User user = new User();
        String getCode = redisTemplate.opsForValue().get(telephone);
        if (getCode.equals(code)) {
            user.setUsername(username);
            user.setPassword(password);
            user.setTelephone(telephone);
            userMapper.register(user);
            result.setCode(ApiResult.VERIFY_SUCCESS.getCode());
            result.setMsg(ApiResult.VERIFY_SUCCESS.getMsg());
            return result;
        } else {
            result.setCode(ApiResult.VERIFY_FAIL.getCode());
            result.setMsg(ApiResult.VERIFY_FAIL.getMsg());
            return result;
        }
    }
}
