package com.iamk.weTeam.common.Enum;

public enum LoginEnum {
    LOGIN_HAS_NOT_LOGIN("50001", "未登录"),
    LOGIN_OVERDUE("50002", "登录过期,请重新登录"),
    ;
    /**
     * 错误码
     */
    private String code;
    /**
     * 错误描述
     */
    private String msg;

    private LoginEnum(String code, String msg) {
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
