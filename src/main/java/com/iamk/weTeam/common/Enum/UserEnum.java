package com.iamk.weTeam.common.Enum;

public enum UserEnum {

    USER_NO_USER("60001", "用户不存在");

    /**
     * 错误码
     */
    private String code;
    /**
     * 错误描述
     */
    private String msg;

    private UserEnum(String code, String msg) {
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
