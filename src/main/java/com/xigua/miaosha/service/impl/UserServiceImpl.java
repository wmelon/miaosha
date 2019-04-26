package com.xigua.miaosha.service.impl;

import com.xigua.miaosha.dao.UserDOMapper;
import com.xigua.miaosha.dao.UserPasswordDOMapper;
import com.xigua.miaosha.dataobject.UserDO;
import com.xigua.miaosha.dataobject.UserPasswordDO;
import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.error.EmBussinessError;
import com.xigua.miaosha.service.UserService;
import com.xigua.miaosha.service.model.UserModel;
import com.xigua.miaosha.validator.ValidatorImpl;
import com.xigua.miaosha.validator.ValidatorResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;


    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null){
            return null;
        }

        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        UserModel userModel = this.convertFromDataOBject(userDO,userPasswordDO);
        return userModel;
    }

    private  UserModel convertFromDataOBject(UserDO userDO, UserPasswordDO userPasswordDO){
        if (userDO == null){
            return null;
        }
        if (userPasswordDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);


        userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        return userModel;
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException{
        if (userModel == null){
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
        }
        ValidatorResult validatorResult = validator.validate(userModel);
        if (validatorResult.isHasError()){
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,validatorResult.getErrMsg());
        }
//        if (StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender() == null
//                || userModel.getAge() == null
//                || StringUtils.isEmpty(userModel.getTelphone())){
//        }

        UserDO userDO = this.convertFromUserModel(userModel);
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException ex){
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"手机号已经注册");
        }

        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = this.convertPasswordFromUserModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException{
        /// 通过用户手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);

        if(userDO == null){
            throw new BusinessException(EmBussinessError.USER_LOGIN_FAIL);
        }

        /// 验证用户密码和传入密码是否一致
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = this.convertFromDataOBject(userDO,userPasswordDO);

        /// 判断传入加密的密码和数据库中的是否一致
        if (!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessException(EmBussinessError.USER_LOGIN_FAIL);
        }

        return userModel;
    }

    private UserPasswordDO convertPasswordFromUserModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());

        return userPasswordDO;
    }
    private UserDO convertFromUserModel(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }
}
