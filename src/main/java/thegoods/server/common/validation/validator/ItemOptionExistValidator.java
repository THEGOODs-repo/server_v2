package thegoods.server.common.validation.validator;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.validation.annotation.ExistItemOption;
import thegoods.server.item.implement.query.ItemQueryService;

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
