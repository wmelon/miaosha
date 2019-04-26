package com.xigua.miaosha.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ValidatorImpl implements InitializingBean {
    /// 实际中的验证类
    private Validator validator;

    /// 显示校验方法并返回校验结果
    public ValidatorResult validate(Object bean){
        final ValidatorResult validatorResult = new ValidatorResult();
        /// 调用框架验证方法返回结果
        Set<ConstraintViolation<Object>> constraintViolationSet  = validator.validate(bean);

        if (constraintViolationSet.size()>0){
            /// 有错误异常
            validatorResult.setHasError(true);
            constraintViolationSet.forEach(constraintViolation->{
                String errMsg = constraintViolation.getMessage();
                String propertyName = constraintViolation.getPropertyPath().toString();
                validatorResult.getErrorMsgMap().put(propertyName,errMsg);
            });
        }
        return validatorResult;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
