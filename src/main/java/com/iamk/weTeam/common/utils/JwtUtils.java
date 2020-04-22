package com.iamk.weTeam.common.utils;


import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.UnicomRuntimeException;
import io.jsonwebtoken.*;
import org.apache.shiro.codec.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT token生成、验证、解析工具类
 */
public class JwtUtils {

    // 生成密钥的字符串
    private static final byte[] SECRET = "WeTeam_IAMK_NWPU_QQ1249248952_".getBytes();
    // // 过期时间 1天*30
    private static final long EXPIRE_TIME = System.currentTimeMillis() + 1000 * 60 * 60 * 12 * 30;

    /** 比如用户输入用户名和密码 若登录合法就生成jwt的一个token 发送给前端 前端存储到localStorage
     *  然后用户每次发起请求都从本地获取 携带着一个token送至后台 去验证  从而完成身份的验证
     * 签发JWT
     * @param id 一般是用户id
     * @param subject 一般是用户名 可以是JSON数据 尽可能少
     * @return
     */
    public static String createJWT(String id, String subject) {
        // 加密算法 HS256 对称加密
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 密钥
        SecretKey secretKey = generalKey();
        // 生成token
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setSubject(subject)                        // 主体可以是json，即要存的信息
                .setIssuer("iamk")                          // 签发者
                .setIssuedAt(new Date())                    // 签发时间
                .setExpiration(new Date(EXPIRE_TIME))       // 过期时间
                // .claim("user", user)                 // 自定义存储信息
                .signWith(signatureAlgorithm, secretKey);   // 签名算法以及密匙
        return builder.compact();
    }

    /**
     * 验证JWT  需要把jwt的token传过来
     * @param token
     * @return
     */
    public static ResultUtil validateJWT(String token) {
        ResultUtil checkResult = new ResultUtil();
        Claims claims = null;
        try {
            claims = parseJWT(token);
            checkResult.setFlag(true);
            checkResult.setStatus("200");
            checkResult.setData(claims);
            return ResultUtil.success(claims);
        } catch (Exception e) {
            System.out.println(e);
        }
        return ResultUtil.error("401", "请重新登录！");
    }

    public static Claims validateJWT2(String token) {
        Claims claims = null;
        try {
            claims = parseJWT(token);
            return claims;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


    /**
     * 解析JWT字符串
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }



    /**
     * 生成加密 Key
     * AES 对称加密
     * 解密时也要用到 因为在刷新token时要通过原来生成的token解密获取用户id 和用户名(subject) 重新生成token
     * @return  SecretKey
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.decode(SECRET);
        SecretKey key = new SecretKeySpec(encodedKey, "AES");
        return key;
    }

    public static Map<String, String> getTokenInfo(String token){
        Map<String, String> map = new HashMap<String, String>();
        // 解析token获取用户信息
        Claims claims = JwtUtils.validateJWT2(token);
        // 解析错误
        if(claims == null){
            System.out.println("claims解析错误");
            throw new UnicomRuntimeException(UnicomResponseEnums.SIGNATURE_NOT_MATCH);
        }
        String userId = (String) claims.get("jti");
        String openId = (String) claims.get("sub");
        map.put("userId", userId);
        map.put("openId", openId);
        return map;
    }



    /**
     * test
     * @param args
     */
    public static void main(String[] args) {
        JwtUtils j = new JwtUtils();
        String jwt = j.createJWT("1", "{id:100, name:k}");
        // System.out.println(jwt);

        // 解析
        Claims claims = null;
        try {
            claims = j.parseJWT(jwt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(claims);
        System.out.println(claims.get("jti"));

        // 验证过期
        // try {
        //     Thread.sleep(10 * 1000);
        //     claims = j.parseJWT(jwt);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        // System.out.println(claims);

        // 验证
        // try {
        //     Thread.sleep(5 * 1000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // ResultUtil resultUtil = j.validateJWT(jwt);
        // System.out.println(resultUtil.getstatus());
    }


}
