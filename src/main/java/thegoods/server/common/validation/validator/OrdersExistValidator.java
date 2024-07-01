package thegoods.server.common.validation.validator;

import com.umc.TheGoods.service.OrderService.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.validation.annotation.ExistOrders;

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
