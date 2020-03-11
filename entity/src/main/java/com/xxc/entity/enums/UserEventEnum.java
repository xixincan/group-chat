package com.xxc.entity.enums;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public enum UserEventEnum {

    REMOVE((byte) -1),
    REGISTER((byte) 0),
    LOGIN((byte) 1),
    LOGOUT((byte) 2),
    ;

    private byte event;

    UserEventEnum(byte event) {
        this.event = event;
    }

    public byte getEvent() {
        return event;
    }
}
