package com.xxc.entity.enums;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public enum ChatTypeEnum {

    ERROR(-1),
    BINDING(0),
    SINGLE_SENDING(1),
    GROUP_SENDING(2),
    FILE_MSG_SINGLE_SENDING(3),
    FILE_MSG_GROUP_SENDING(4),
    ;

    private int type;

    ChatTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static ChatTypeEnum find(Integer type) {
        ChatTypeEnum[] values = ChatTypeEnum.values();
        for (ChatTypeEnum item : values) {
            if (Integer.valueOf(item.getType()).equals(type)) {
                return item;
            }
        }
        return ERROR;
    }
}
