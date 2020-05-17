package com.iamk.weTeam.model.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送模板消息通用实体
 */
@Data
public class GZHMessageSendDTO {
    /**
     * 接收者openid
     */
    private String touser;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 模板跳转链接（海外帐号没有跳转能力）
     */
    private String url;

    /**
     * 跳小程序所需数据，不需跳小程序可不用传该数据
     */
    private String miniProgram;
    // private Map<String, String> miniProgram;

    /**
     * 所需跳转到的小程序appid
     */
    private String appId;

    /**
     * 所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar）
     */
    private String pagePath;

    /**
     * 模板数据
     */
    // private String data;
    private Map<String, Map<String, String>> data;

    /**
     * JWID
     */
    private String jwid;

    public static Map<String, String> initData(String value, String color) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("value", value);
        data.put("color", color);
        return data;
    }
}
