package com.klay.controller;

import com.klay.api.ApiResult;
import com.klay.entity.Admin;
import com.klay.entity.User;
import com.klay.feign.AccountFeign;
import com.klay.service.SmsService;
import com.netflix.discovery.converters.Auto;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: KlayHu
 * @create: 2019/12/18 17:34
 **/
@Controller
@RequestMapping("/account")
@Log
public class AccountController {

    @Autowired
    private AccountFeign accountFeign;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password,
                        @RequestParam("type") String type, HttpSession session){
        Object object = accountFeign.login(username, password, type);
        LinkedHashMap<String,Object> hashMap =(LinkedHashMap)object;
        String result = null;
        if(object==null){
            result = "login";
        }else {
            switch (type){
                case "user":
                    User user = new User();
                    String idStr = hashMap.get("id")+"";
                    long id = Long.parseLong(idStr);
                    String nickname = (String) hashMap.get("nickname");
                    user.setId(id);
                    user.setNickname(nickname);
                    session.setAttribute("user",user);
                    result = "index";
                    break;
                case "admin":
                    Admin admin = new Admin();
                    String id1 = hashMap.get("id")+"";
                    long Id = Long.parseLong(id1);
                    String userName = (String) hashMap.get("username");
                    admin.setId(Id);
                    admin.setUsername(userName);
                    session.setAttribute("admin",admin);
                    result = "main";
                    break;
            }
        }
        return result;
    }
    @GetMapping("/redirect/{location}")
    public String redirect(@PathVariable("location")String location){
        return location;
    }

    @RequestMapping(value = "/getSmsCode")
    public String getCode(){
        return "/verify/verifyLogin";
    }

    /**
     * 验证码发送
     *
     * @param phone
     * @param session
     * @return
     */
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult sendCode(@RequestParam(value = "phone", required = false) String phone, HttpSession session) {
        ApiResult result = new ApiResult();
        String getPhone = accountFeign.sendSms(phone);
        if (getPhone == null) {
            return null;
        }
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)) {
            //todo 验证码未过期，页面处理
            log.info("===========================验证码还未过期，请等待60s后重新发送===========================");
        }
        code = String.valueOf(Math.random() * 10000).substring(0, 4);
        HashMap<String, Object> param = new HashMap<>();
        param.put("code", code);
        boolean isSend = smsService.send(phone, "SMS_205471000", param);
        if (isSend) {
            redisTemplate.opsForValue().set(phone, code, 1, TimeUnit.MINUTES);
            result.setCode(ApiResult.SEND_SUCCESS.getCode());
            result.setMsg(ApiResult.SEND_SUCCESS.getMsg());
            log.info("==============手机号：" + phone + " 验证码：" + code + " 发送成功！===============");
            session.setAttribute("code", code);
            return result;
        }
        log.info("==========================验证码发送失败=========================");
        result.setCode(ApiResult.SEND_FAIL.getCode());
        result.setMsg(ApiResult.SEND_FAIL.getMsg());
        return result;
    }

    /**
     * 验证码校验
     * @param code
     * @param session
     * @return
     */
    @RequestMapping(value = "/verifyCode",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult verifyCode(@RequestParam(value = "verifyCode" , required = false) String code, HttpSession session,
                                User user) {
        ApiResult result = new ApiResult();
        if (session.getAttribute("code").toString() == null) {
            result.setCode(ApiResult.VERIFY_FAIL.getCode());
            result.setMsg(ApiResult.VERIFY_FAIL.getMsg());
            return result;
        }
        String getCode = session.getAttribute("code").toString();
        if (getCode.equals(code)) {
            accountFeign.verifySms(user, code, session);
            result.setCode(ApiResult.VERIFY_SUCCESS.getCode());
            result.setMsg(ApiResult.VERIFY_SUCCESS.getMsg());
            return result;
        }
        else {
            return result;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();   //销毁session
        return "redirect:/account/redirect/login";
    }

}
