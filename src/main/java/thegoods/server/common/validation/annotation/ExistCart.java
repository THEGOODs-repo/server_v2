package thegoods.server.common.validation.annotation;

import thegoods.server.common.validation.validator.CartExistValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CartExistValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistCart {
    String message() default "해당 장바구니 내역을 찾을 수 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
