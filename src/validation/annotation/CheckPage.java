package com.umc.TheGoods.validation.annotation;

import com.umc.TheGoods.validation.validator.CheckPageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckPageValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPage {
    String message() default "잘못된 페이지 입력 값 입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
