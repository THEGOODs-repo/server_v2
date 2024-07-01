package thegoods.server.common.validation.validator;

//import com.umc.TheGoods.service.TagService.TagQueryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.validation.annotation.ExistTag;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TagExistValidator implements ConstraintValidator<ExistTag, List<Long>> {

    private final TagQueryService tagQueryService;

    @Override
    public void initialize(ExistTag constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<Long> values, ConstraintValidatorContext context) {
        boolean isValid = values.stream()
                .allMatch(value -> tagQueryService.existTagById(value));

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.TAG_NOT_FOUND.getMessage()).addConstraintViolation();
        }

        return isValid;
    }
}

