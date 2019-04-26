package com.xigua.miaosha.controller;

import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.error.EmBussinessError;
import com.xigua.miaosha.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_JAVASCRIPT = "text/javascript";
    public static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    /// 定义exceptionhandler解决未被controller层处理的问题
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        Map<String,Object> responseData = new HashMap<>();
        if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;

            responseData.put("errCode",businessException.getErrorCode());
            responseData.put("errMsg",businessException.getErrorMsg());
        } else  {
            responseData.put("errCode", EmBussinessError.UNKONW_ERROR.getErrorCode());
            responseData.put("errMsg",EmBussinessError.UNKONW_ERROR.getErrorMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }
}
