package com.iamk.weTeam.common.Enum;

public enum ActivityEnum {

    ACTIVITY_NOT_EXIT("40001", "活动不存在");

    /**
     * 错误码
     */
    private String code;
    /**
     * 错误描述
     */
    private String msg;

    private ActivityEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
