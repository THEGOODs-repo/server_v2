package thegoods.server.post.exception.handler;

import thegoods.server.common.exception.GeneralException;
import thegoods.server.common.exception.status.ErrorStatus;

public class PostHandler extends GeneralException {

    public PostHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
