package com.klay.feign;

import com.klay.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: KlayHu
 * @create: 2019/12/18 17:41
 **/
@FeignClient(value = "ACCOUNT")
public interface AccountFeign {

    //登录
    @GetMapping("/account/login/{username}/{password}/{type}")
    public Object login(@PathVariable("username") String username, @PathVariable("password") String password,
                        @PathVariable("type") String type);

    //发送验证码
    @RequestMapping(value = "/account/sendCode",method = RequestMethod.POST)
    public String sendSms(@RequestParam("phone") String phone);

    //验证码校验
    @RequestMapping(value = "/account/verifyCode",method = RequestMethod.POST)
    public String verifySms(@RequestBody User user,@RequestParam("code") String code,@RequestParam("session") HttpSession session);
}
