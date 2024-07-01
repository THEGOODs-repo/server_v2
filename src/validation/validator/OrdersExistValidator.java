package com.umc.TheGoods.validation.validator;

import com.umc.TheGoods.apiPayload.code.status.ErrorStatus;
import com.umc.TheGoods.service.OrderService.OrderQueryService;
import com.umc.TheGoods.validation.annotation.ExistOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class OrdersExistValidator implements ConstraintValidator<ExistOrders, Long> {
    private final OrderQueryService orderQueryService;

    @Override
    public void initialize(ExistOrders constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = orderQueryService.isExistOrders(value);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ORDER_NOT_FOUND.getMessage()).addConstraintViolation();
        }
        return isValid;
    }
}
