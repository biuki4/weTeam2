package com.iamk.weTeam.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.codec.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取小程序openId、session_key
 **/

public class WeChatUtil {


    /**
     * 获取sessionKey、 openId
     * @param code
     * @param appid
     * @param appsecret
     * @param url
     * @return
     */
    public static JSONObject getSessionKeyOpenId(String code, String appid, String appsecret, String url) {

        // 发送请求获取session_key openId
        Map<String, String> params = new HashMap<>();
        //小程序appId
        params.put("appid", appid);
        //小程序secret
        params.put("secret", appsecret);
        //小程序端返回的code
        params.put("js_code", code);
        //默认参数
        params.put("grant_type", "authorization_code");
        // 发送请求获取openId 、 sessionKey
        return JSON.parseObject(HttpClientUtil.doPost(url, params));
    }

    /**
     * 解密授权后返回的用户信息
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    public static JSONObject getUserInfo(String encryptedData, String sessionKey, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return JSON.parseObject(result);
            }
        } catch (Exception e) {
        }
        return null;
    }

}
