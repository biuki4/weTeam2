package com.iamk.weTeam.common.utils;

import com.iamk.weTeam.common.Enum.*;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResultUtil implements Serializable {

    // 结果标记
    private Boolean flag;

    // 消息状态码
    private String status;

    // 消息
    private String msg;

    // 返回数据
    private Object data;

    public ResultUtil() {
    }

    public ResultUtil(boolean flag, Object data) {
        super();
        this.flag = flag;
        this.data = data;
    }

    public ResultUtil(boolean flag, String status, String msg) {
        super();
        this.flag = flag;
        this.status = status;
        this.msg = msg;
    }

    public ResultUtil(Boolean flag, String status, String msg, Object data) {
        this.flag = flag;
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ResultUtil(boolean flag, UnicomResponseEnums enums) {
        this.flag = flag;
        this.status = enums.getCode();
        this.msg = enums.getMsg();
    }

    public ResultUtil(UnicomResponseEnums enums) {
        this.flag = false;
        this.status = enums.getCode();
        this.msg = enums.getMsg();
    }

    public ResultUtil(boolean flag, Object data, UnicomResponseEnums enums) {
        this.flag = flag;
        this.status = enums.getCode();
        this.msg = enums.getMsg();
        this.data = data;
    }

    /**
     * 响应成功
     *
     * @param data 返回数据
     * @return ResultUtil
     */
    public static ResultUtil success(Object data) {
        return new ResultUtil(true, "200", "响应成功", data);
    }

    /**
     * 响应成功
     *
     * @param data 返回数据
     * @param msg  提示信息
     * @return
     */
    public static ResultUtil success(Object data, String msg) {
        return new ResultUtil(true, "200", msg, data);
    }

    public static ResultUtil success() {
        return new ResultUtil(true, "200", "响应成功", null);
    }

    public static ResultUtil success(UnicomResponseEnums enums) {
        return new ResultUtil(true, "200", enums.getMsg(), null);
    }

    public static ResultUtil success(UnicomResponseEnums enums, Object data) {
        return new ResultUtil(true, "200", enums.getMsg(), data);
    }

    public static ResultUtil success(UnicomResponseEnums enums, String data) {
        return new ResultUtil(true, "200", enums.getMsg(), data);
    }

    /**
     * 响应失败
     *
     * @param status 状态码
     * @param msg    提示消息
     * @return
     */
    public static ResultUtil error(String status, String msg) {
        return new ResultUtil(false, status, msg, null);
    }

    public static ResultUtil error(UnicomResponseEnums enums) {
        return new ResultUtil(false, enums.getCode(), enums.getMsg(), null);
    }

    public static ResultUtil error(GameEnum enums) {
        return new ResultUtil(false, enums.getCode(), enums.getMsg(), null);
    }

    public static ResultUtil error(ActivityEnum enums) {
        return new ResultUtil(false, enums.getCode(), enums.getMsg(), null);
    }

    public static ResultUtil error(AdminEnum enums) {
        return new ResultUtil(false, enums.getCode(), enums.getMsg(), null);
    }

    public static ResultUtil error(TeamEnum enums) {
        return new ResultUtil(false, enums.getCode(), enums.getMsg(), null);
    }

    public static ResultUtil error(LoginEnum enums) {
        return new ResultUtil(false, enums.getCode(), enums.getMsg(), null);
    }

    public static ResultUtil error(UserEnum enums) {
        return new ResultUtil(false, enums.getCode(), enums.getMsg(), null);
    }
}
