package com.hmdp.domain.dto;

import com.hmdp.valid.PhoneNumber;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginFormDTO {

    @PhoneNumber
    private String phone;

    @Length(min = 6, max = 6, message = "验证码不合法")
    private String code;

    @NotNull
    private String password;
}
