package thegoods.server.member.exception.handler;

import thegoods.server.common.exception.GeneralException;
import thegoods.server.common.exception.status.ErrorStatus;

public class MemberHandler extends GeneralException {

    public MemberHandler(ErrorStatus errorCode) {
        super(errorCode);
    }
}
