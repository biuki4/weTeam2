package com.iamk.weTeam.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.constant.MiniProgramConstant;
import com.iamk.weTeam.common.constant.UnionConstant;
import com.iamk.weTeam.model.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MyUtils {

    public static String UNION_URL = "https://www.ilovenpu.com";

    /**
     * 将json字符串数组转换为对应的Integer数组
     * @param ids
     * @return
     */
    public static Integer[] toIntegerArray(String ids){
        JSONArray json = JSONObject.parseArray(ids);
        Integer[] a = new Integer[json.size()];
        // json.stream().forEach(System.out::println);
        Integer[] array = json.toArray(a);
        return array;
    }

    /**
     * 将json字符串数组转换为对应的String数组
     * @param ids
     * @return
     */
    public static String[] toStringArray(String ids){
        JSONArray json = JSONObject.parseArray(ids);
        String[] a = new String[json.size()];
        // json.stream().forEach(System.out::println);
        String[] array = json.toArray(a);
        return array;
    }

    /**
     * 获取token中的useId
     * @param token
     * @return
     */
    public static Integer getUserIdFromToken(String token) {
        Map<String, String> tokenInfo = JwtUtils.getTokenInfo(token);
        return Integer.parseInt(tokenInfo.get("userId"));
    }

    /**
     * 获取token的OpenId
     * @param token
     * @return
     */
    public static String getOpenIdFromToken(String token) {
        Map<String, String> tokenInfo = JwtUtils.getTokenInfo(token);
        return tokenInfo.get("openId");
    }

    /**
     * 创建队伍编号
     * @param t
     * @return
     */
    public static String createTeamNo(Team t) {
        StringBuffer buffer = new StringBuffer();
        String s = DateUtil.format2(new Date());
        buffer.append("WT");
        buffer.append(s);
        buffer.append(t.getUserId());
        buffer.append(t.getGameId());
        return buffer.toString();
    }

    /**
     * sha1 加密
     * @param str
     * @return
     */
    public static String sha1(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            // 创建 16进制字符串
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据unionId获取i瓜大用户信息
     * @param secret    颁发给开发者的密钥
     * @param appId     小程序的appid
     * @param unionId   通过 wx.login 获取到的用户的unionId
     * @return
     */
    public static JSONObject getUserFromUnionId(String secret, String appId, String unionId) {
        log.info("decode info by unionId");
        Map<String, String> params = new HashMap<>();
        params.put("weappid", appId);
        // sign
        String str1 = MD5Util.md5(secret + appId + secret);
        String sign = sha1(str1 + unionId);
        params.put("sign", sign);
        // unionId
        params.put("weapp_unionid", unionId);
        // System.out.println(params.toString());
        JSONObject jsonObject = JSON.parseObject(HttpClientUtil.doPost(UNION_URL + "/api/union/weapp/authorizations", params));
        log.info("decode success");
        return jsonObject;
    }

    public static void main(String[] args) {
        JSONObject userFromUnionId = getUserFromUnionId(UnionConstant.WEAPPSECRET, MiniProgramConstant.APPID, "oySnI1KkVx229hXS78T5MHY6TVWw");
        System.out.println(userFromUnionId);
        System.out.println(userFromUnionId.get("school_account"));
        Object school_account = userFromUnionId.get("school_account");

        JSONObject y = null;
        System.out.println(y.toJSONString());
    }
}
