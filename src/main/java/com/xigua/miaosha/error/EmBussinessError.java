package com.xigua.miaosha.error;


public enum EmBussinessError implements CommonError {
    // 通用类型定义
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKONW_ERROR(10002,"未知错误"),

    // 20000 开头为用户信息相关错误码
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAIL(20002,"用户手机号或密码不正确"),

    // 30000开头为秒杀相关错误码
    SECKILL_UNDERSTOCK(30001,"商品库存不足")
    ;


    private EmBussinessError(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    private int errCode;
    private String errMsg;

    @Override
    public int getErrorCode() {
        return this.errCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errMsg = errorMsg;
        return this;
    }
}
