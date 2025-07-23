package com.hmdp.valid.validator;

import com.hmdp.valid.PhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义手机号码校验器
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null
                && value.matches("^1(3[0-9]|4[57]|5[0-35-9]|8[0-9]|70)\\d{8}$")
                && value.length() == 11;
    }
}
