package com.xxc.common.enums;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
public enum IpPlanEnum {

    ALL_ACCESS(0),
    WHITE_ACCESS(1),
    BLACK_DENIED(2);

    private int strategy;

    IpPlanEnum(int strategy) {
        this.strategy = strategy;
    }

    public int getStrategy() {
        return strategy;
    }

    public static IpPlanEnum find(Integer strategy) {
        if (null != strategy) {
            IpPlanEnum[] values = IpPlanEnum.values();
            for (IpPlanEnum item : values) {
                if (item.getStrategy() == strategy) {
                    return item;
                }
            }
        }
        return ALL_ACCESS;
    }
}
