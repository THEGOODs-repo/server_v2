package thegoods.server.common.validation.validator;

//import com.umc.TheGoods.domain.member.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import thegoods.server.common.exception.status.ErrorStatus;
import thegoods.server.common.validation.annotation.CheckTempMember;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@RequiredArgsConstructor
public class CheckTempMemberValidator implements ConstraintValidator<CheckTempMember, Object> {
    @Override
    public void initialize(CheckTempMember constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof Member) {
            Member member = (Member) value;
            if (member.getId().equals(0L)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(ErrorStatus.MEMBER_NOT_FOUND.toString()).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
