package com.xxc.entity.enums;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public enum GroupStatusEnum {

    ABANDON(-1),
    DISABLED(0),
    NORMAL(1),
    ;

    private int status;

    GroupStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
