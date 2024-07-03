package thegoods.server.order.exception;

import thegoods.server.common.exception.BaseErrorCode;
import thegoods.server.common.exception.GeneralException;

public class OrderHandler extends GeneralException {
    public OrderHandler(BaseErrorCode code) {
        super(code);
    }
}
