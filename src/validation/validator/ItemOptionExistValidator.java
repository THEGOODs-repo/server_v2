package com.umc.TheGoods.validation.validator;

import com.umc.TheGoods.apiPayload.code.status.ErrorStatus;
import com.umc.TheGoods.service.ItemService.ItemQueryService;
import com.umc.TheGoods.validation.annotation.ExistItemOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class ItemOptionExistValidator implements ConstraintValidator<ExistItemOption, Long> {

    private final ItemQueryService itemQueryService;

    @Override
    public void initialize(ExistItemOption constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        boolean isValid = itemQueryService.isExistItemOption(value);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ITEMOPTION_NOT_FOUND.getMessage()).addConstraintViolation();
        }
        return isValid;
    }
}
