package com.klay.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: result返回结果集Api
 * @author: KlayHu
 * @create: 2020/12/6 20:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResult {
    /**
     * 状态码，200---成功，500---服务器请求错误
     */
    private int code;

    /**
     * 状态信息，通过json返回数据给前端
     */
    private String msg;

    //sms验证码发送
    public static ApiResult SEND_SUCCESS = new ApiResult(200,"验证码发送成功，请注意查收!");
    public static ApiResult SEND_FAIL = new ApiResult(500,"服务器故障，请稍后重试!");

    //sms验证码校验
    public static ApiResult VERIFY_SUCCESS = new ApiResult(200, "验证通过，正在跳转...");
    public static ApiResult VERIFY_FAIL = new ApiResult(500,"验证码错误，请输入正确的验证码!");
}
