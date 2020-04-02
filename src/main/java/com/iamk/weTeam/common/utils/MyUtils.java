package com.iamk.weTeam.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.model.entity.Team;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

public class MyUtils {

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
}
