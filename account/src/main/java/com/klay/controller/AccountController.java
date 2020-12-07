package com.klay.controller;

import com.klay.entity.Admin;
import com.klay.entity.User;
import com.klay.mapper.AdminMapper;
import com.klay.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: KlayHu
 * @create: 2019/12/18 15:20
 **/

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;

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
    @RequestMapping(value = "/sendCode",method = RequestMethod.POST)
    public String sendSms(String phone){
        if (phone != null || phone.length()!=0) {
            return phone;
        }
        return null;
    }

    /**
     * 验证码校验
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.POST)
    public User verifySms(@RequestBody User user, String code,
                            HttpSession session) {
        if (code.equals(session.getAttribute("code"))) {
            userMapper.register(user);
        }
        return null;
    }
}
