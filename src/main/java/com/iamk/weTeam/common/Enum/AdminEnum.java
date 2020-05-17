package com.iamk.weTeam.common.Enum;

public enum AdminEnum {

    Admin_NOT_ADMIN("20001", "您不是管理员，没有权限"),
    ADMIN_NO_RIGHT("20002", "权限不足");

    /**
     * 错误码
     */
    private String code;
    /**
     * 错误描述
     */
    private String msg;

    private AdminEnum(String code, String msg) {
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
