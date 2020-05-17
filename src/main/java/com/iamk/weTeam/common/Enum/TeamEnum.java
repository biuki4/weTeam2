package com.iamk.weTeam.common.Enum;

public enum TeamEnum {

    TEAM_FULL("10001", "队伍已满"),
    TEAM_HAS_APPLY("10002", "请勿重复申请"),
    TEAM_HAS_CREATE("10003", "请勿重复创建"),
    TEAM_NOT_LEADER("10004", "您不是该队队长"),
    TEAM_HAS_JOIN_OTHER("10005", "您已加入其他队伍"),
    TEAM_NOT_EXIST("10006", "队伍不存在"),
    TEAM_TIME_LIMIT("10007", "操作频繁，请稍后再试"),
    TEAM_CANCEL_TOO_MANY("10008", "取消次数过多，存在骚扰行为，无法再次申请"),
    TEAM_NO_RECORD("10009", "没有申请记录，请刷新后重试")
    ;

    /**
     * 错误码
     */
    private String code;
    /**
     * 错误描述
     */
    private String msg;

    private TeamEnum(String code, String msg) {
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
