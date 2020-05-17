package com.iamk.weTeam.wxMp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {

    private List<MpConfig> configs;

    private String myOpenId;

    @Data
    public static class MpConfig {
        /**
         * 设置微信公众号的appid
         */
        private String appId;

        /**
         * 设置微信公众号的app secret
         */
        private String secret;

        /**
         * 设置微信公众号的token
         */
        private String token;

        /**
         * 设置微信公众号的EncodingAESKey
         */
        private String aesKey;
        /**
         * 申请加入的模板id
         */
        private String applyTemplateId;

        /**
         * 通知模板id
         */
        private String informTemplateId;
    }


}
