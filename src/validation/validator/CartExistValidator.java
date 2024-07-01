package com.umc.TheGoods.validation.validator;

import com.umc.TheGoods.apiPayload.code.status.ErrorStatus;
import com.umc.TheGoods.service.CartService.CartQueryService;
import com.umc.TheGoods.validation.annotation.ExistCart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class CartExistValidator implements ConstraintValidator<ExistCart, Long> {

    private final CartQueryService cartQueryService;

    @Override
    public void initialize(ExistCart constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = cartQueryService.isExistCart(value);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.CART_NOT_FOUND.getMessage()).addConstraintViolation();
        }
        return isValid;
    }
}
