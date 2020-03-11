package com.xxc.common.enums;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public enum UserStatusEnum {

    ABANDON(-1),
    FREEZE(0),
    NORMAL(1),
    ;

    private int status;

    UserStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
