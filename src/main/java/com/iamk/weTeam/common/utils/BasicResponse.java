package com.iamk.weTeam.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicResponse<T> {

    @JsonProperty
    private boolean success;

    @JsonProperty
    private String message;

    @JsonProperty
    private T data;

    public BasicResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static <T> BasicResponse<T> ok() {
        return new BasicResponse<>(true, "操作成功");
    }

    public static <T> BasicResponse<T> fail() {
        return new BasicResponse<>(false, "操作失败");
    }

    public String message() {
        return message;
    }

    public BasicResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    public T data() {
        return data;
    }

    public BasicResponse<T> data(T data) {
        this.data = data;
        return this;
    }
}
