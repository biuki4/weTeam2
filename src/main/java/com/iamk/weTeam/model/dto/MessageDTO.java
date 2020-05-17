package com.iamk.weTeam.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MessageDTO {

    @JsonProperty("touser")
    private String toUser;

    @JsonProperty("template_id")
    private String templateId;

    private String url;

    // private Map<String, String> miniProgram;

    private Map<String, Map<String, String>> data = new HashMap<>();

    public static Map<String, String> initData(String value) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("value", value);
        data.put("color", "");
        return data;
    }

    public static Map<String, String> initData(String value, String color) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("value", value);
        data.put("color", color);
        return data;
    }
}
