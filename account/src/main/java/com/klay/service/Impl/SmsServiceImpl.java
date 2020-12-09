package com.klay.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.klay.service.SmsService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description: sms短信验证码实现类
 * @author: KlayHu
 * @create: 2020/12/6 20:13
 **/
@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean send(String phoneNum, String templateCode, Map<String, Object> code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4GCJR7gJsx2qAJxns7ri", "yWL6PSRXWxJBTr8LLFYOUcQaFLtsia");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("SignName", "ISIP管理系统");
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(code));
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            return response.getHttpResponse().isSuccess();
        } catch (com.aliyuncs.exceptions.ServerException e) {
            e.printStackTrace();
        } catch (com.aliyuncs.exceptions.ClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
