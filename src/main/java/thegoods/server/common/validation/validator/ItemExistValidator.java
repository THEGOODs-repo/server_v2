package thegoods.server.common.validation.validator;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.validation.annotation.ExistItem;
import thegoods.server.item.implement.query.ItemQueryService;

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
