package com.xigua.miaosha.response;

public class CommonReturnType {

    //  请求状态 success  or error
    private String status;
    // 如果status 为 success  data为正确返回数据  status 为error 为通用错误格式
    private Object data;


    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result,String status){
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
