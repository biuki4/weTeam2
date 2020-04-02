package com.iamk.weTeam.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {
    // appId
    public String APPID;

    // appSecret
    public String APPSECRET;

    // request url
    public String URL;

}

