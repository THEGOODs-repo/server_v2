package thegoods.server.item.exception;

import thegoods.server.common.exception.BaseErrorCode;
import thegoods.server.common.exception.GeneralException;

public class TagHandler extends GeneralException {
    public TagHandler(BaseErrorCode code) {
        super(code);
    }
}
