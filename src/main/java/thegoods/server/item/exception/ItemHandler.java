package thegoods.server.item.exception;

import thegoods.server.common.exception.BaseErrorCode;
import thegoods.server.common.exception.GeneralException;

public class ItemHandler extends GeneralException {
    public ItemHandler(BaseErrorCode code) {
        super(code);
    }
}
