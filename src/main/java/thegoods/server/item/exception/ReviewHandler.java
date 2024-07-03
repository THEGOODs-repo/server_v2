package thegoods.server.item.exception;

import thegoods.server.common.exception.BaseErrorCode;
import thegoods.server.common.exception.GeneralException;

public class ReviewHandler extends GeneralException {
    public ReviewHandler(BaseErrorCode code) {
        super(code);
    }
}
