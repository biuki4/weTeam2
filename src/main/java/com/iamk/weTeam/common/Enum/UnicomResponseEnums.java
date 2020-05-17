package com.iamk.weTeam.common.Enum;


public enum UnicomResponseEnums {

    SUCCESS("200", "成功!"),
    BODY_NOT_MATCH("400", "请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH("401", "请求的数字签名不匹配!"),
    NOT_FOUND("404", "未找到该资源!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    REQUEST_METHOD_SUPPORT_ERROR("40001","当前请求方法不支持"),

    SYSTEM_ERROR("-001","系统异常"),
    BAD_REQUEST("-002","错误的请求参数"),
    // NOT_FOUND("-003","找不到请求路径！"),
    CONNECTION_ERROR("-004","网络连接请求失败！"),
    METHOD_NOT_ALLOWED("-005","不合法的请求方式"),
    DATABASE_ERROR("-004","数据库异常"),
    BOUND_STATEMENT_NOT_FOUNT("-006","找不到方法！"),
    FAIL_LIMIT_LOGIN("001", "已被限制登录,5分钟之后再试！"),
    FAIL_FORBID_LOGIN("002","已被禁止登录，请联系管理员"),
    LOGIN_DATED("003", "登录失效，请重新登录！"),
    NO_USER_EXIST("004","用户不存在"),
    UPDATE_SUCCESS("005", "修改成功"),
    UPDATE_FAIL("006", "修改失败"),
    FILEUPLOAD_SUCCESS("007","上传成功"),
    FILEUPLOAD_FAIL("008","上传失败"),
    NO_PERMISSION("009", "权限不足"),

    // user


    // common
    SERVER_BUSY("0001", "服务器正忙，请稍后再试!"),

    // team
    TEAM_FULL("1001", "队伍已满"),
    HAS_APPLY("1002", "请勿重复申请"),
    HAS_CREATE("1003", "请勿重复创建"),
    NOT_LEADER("1004", "您不是该队队长"),
    HAS_TEAM("1005", "您已加入其他队伍"),
    T_NO_TEAM("1006", "队伍不存在"),
    T_TIME_LIMIT("1007", "两分钟内无法再次申请"),

    // admin
    NOT_ADMIN("2001", "您不是管理员，没有权限"),
    NO_RIGHT("2002", "权限不足"),
    UPLOAD_FAIL("2003", "文件上传失败"),
    NO_FILE("2004", "未识别文件"),

    // game
    NO_GAME("3001", "竞赛已删除");


    // REPEAT_REGISTER("001","重复注册"),
    // NO_USER_EXIST("002","用户不存在"),
    // INVALID_PASSWORD("003","密码错误"),
    // NO_PERMISSION("004","非法请求！"),
    // SUCCESS_OPTION("005","操作成功！"),
    // NOT_MATCH("-007","用户名和密码不匹配"),
    // FAIL_GETDATA("-008","获取信息失败"),
    // BAD_REQUEST_TYPE("-009","错误的请求类型"),
    // INVALID_MOBILE("010","无效的手机号码"),
    // INVALID_EMAIL("011","无效的邮箱"),
    // INVALID_GENDER("012","无效的性别"),
    // REPEAT_MOBILE("014","已存在此手机号"),
    // REPEAT_EMAIL("015","已存在此邮箱地址"),
    // NO_RECORD("016","没有查到相关记录"),
    // LOGIN_SUCCESS("017","登陆成功"),
    // LOGOUT_SUCCESS("018","已退出登录"),
    // SENDEMAIL_SUCCESS("019","邮件已发送，请注意查收"),
    // EDITPWD_SUCCESS("020","修改密码成功"),
    // No_FileSELECT("021","未选择文件"),
    // NOLOGIN("023","未登陆"),
    // ILLEGAL_ARGUMENT("024","参数不合法"),
    // ERROR_IDCODE("025","验证码不正确");

    /**
     * 错误码
     */
    private String code;
    /**
     * 错误描述
     */
    private String msg;

    private UnicomResponseEnums(String code, String msg) {

        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
