package com.klay.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description: 短信验证码接口 【使用aliyun子账号】
 * @author: KlayHu
 * @create: 2020/12/6 20:10
 **/
public interface SmsService {
    boolean send(String phoneNum, String templateCode, Map<String, Object> code);
}
