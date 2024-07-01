package thegoods.server.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorReasonDTO {

    private final HttpStatus httpStatus;

    private final boolean isSuccess;
    private final String code;
    private final String message;

    private final Integer status;
    private final String reason;

    public boolean getIsSuccess() {
        return isSuccess;
    }
}
