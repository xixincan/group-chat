package com.xxc.entity.enums;

/**
 * @author xixincan
 * 2020-03-13
 * @version 1.0.0
 */
public enum UserChatStatusEnum {

    //ONLINE > 0

    OFFLINE(0),
    FREE(1),
    LEAVE(2),
    BUSY(3),
    ;

    private int status;

    UserChatStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
