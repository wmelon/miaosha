package com.xigua.miaosha.controller;


import com.xigua.miaosha.controller.viewobject.UserVO;
import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.error.EmBussinessError;
import com.xigua.miaosha.response.CommonReturnType;
import com.xigua.miaosha.service.UserService;
import com.xigua.miaosha.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@Api(tags ="用户")
@Controller("user")
@RequestMapping("/user")
@CrossOrigin
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /// 获取验证码接口
    @RequestMapping(value = "/getOtp",method = {RequestMethod.GET})
    @ResponseBody
    @ApiOperation(value = "获取验证码")
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone){

        /// 按照规则生成验otp证码
        Random random = new Random();
        int randomInt = random.nextInt(999999);
        randomInt += 100000;
        String otpCode = String.valueOf(randomInt);

        /// 验证码和用户手机号关联  ,实际项目中用redis 中
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        /// otp验证码以短信发送到用户手机  ，省略
        System.out.println("telphone = " + telphone + "& otpCode = " + otpCode );

        /// 返回登录用户信息
        return CommonReturnType.create(null);
    }

    /// 用户注册
    @RequestMapping(value = {"/register"}, method={RequestMethod.POST},consumes={CONTENT_TYPE_APPLICATION_JSON}, produces={CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    @ApiOperation(value = "用户注册接口")
    public CommonReturnType register(@RequestBody UserModel userModel) throws BusinessException {
//        otpCode = "123456";

//        /// 手机号和验证码是否符合
////        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
////        if (!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
////            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"短信验证码错误");
////        }

        /// 用户注册流程
        userService.register(userModel);

        return CommonReturnType.create(null);
    }

    /// 用户登录
    @RequestMapping(value = {"/login"}, method={RequestMethod.POST},consumes={CONTENT_TYPE_APPLICATION_JSON}, produces={CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    @ApiOperation(value = "用户登录接口")
    public CommonReturnType login(@RequestBody UserModel userModel) throws BusinessException{
        String telphone =  userModel.getTelphone();
        String encrptPassword = userModel.getEncrptPassword();
        /// 入参校验
        if (StringUtils.isEmpty(telphone)){
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,"用户手机号不能为空");
        }
        /// 用户登录服务
        UserModel resultUserModel = userService.validateLogin(telphone,encrptPassword);
        return CommonReturnType.create(resultUserModel);
    }

    /// 获取用戶信息接口
    @RequestMapping(value = "/getUser",method = {RequestMethod.GET})
    @ResponseBody
    @ApiOperation(value = "获取用户信息接口")
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        // 调用service 服务获取对应的id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);

        if (userModel == null) {
            throw new BusinessException(EmBussinessError.USER_NOT_EXIST);
        }


        UserVO userVO = this.convertFromUserModel(userModel);
        return CommonReturnType.create(userVO);
    }
    private UserVO convertFromUserModel(UserModel userModel){
        if (userModel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }


}
