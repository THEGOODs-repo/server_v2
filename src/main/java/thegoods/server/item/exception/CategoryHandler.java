package thegoods.server.item.exception;

import thegoods.server.common.exception.BaseErrorCode;
import thegoods.server.common.exception.GeneralException;

public class CategoryHandler extends GeneralException {
    public CategoryHandler(BaseErrorCode code) {
        super(code);
    }
}
