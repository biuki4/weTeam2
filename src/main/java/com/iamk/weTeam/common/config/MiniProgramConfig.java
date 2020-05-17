package com.iamk.weTeam.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 小程序相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class MiniProgramConfig {
    // appId
    public String APPID;

    // appSecret
    public String APPSECRET;

    // request url
    public String URL;

}

