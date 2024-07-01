package com.umc.TheGoods.validation.validator;

import com.umc.TheGoods.apiPayload.code.status.ErrorStatus;
import com.umc.TheGoods.service.ItemService.ItemQueryService;
import com.umc.TheGoods.validation.annotation.ExistItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class ItemExistValidator implements ConstraintValidator<ExistItem, Long> {

    private final ItemQueryService itemQueryService;

    @Override
    public void initialize(ExistItem constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = itemQueryService.isExistItem(value);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ITEM_NOT_FOUND.toString()).addConstraintViolation();
        }
        return isValid;
    }
}
