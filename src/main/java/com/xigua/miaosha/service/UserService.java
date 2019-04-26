package com.xigua.miaosha.service;


import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.service.model.UserModel;

public interface UserService {

    UserModel getUserById(Integer id);

    void register(UserModel userModel) throws BusinessException;
    /*
    * encrptPassword 用户加密后的密码
    * */
    UserModel validateLogin (String telphone,String encrptPassword) throws BusinessException;
}
