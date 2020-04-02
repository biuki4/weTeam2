package com.iamk.weTeam.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.iamk.weTeam.model.entity.User;
import org.springframework.stereotype.Service;

/**
 * 生成token
 */
@Service("TokenService")
public class TokenService {

    public String getToken(User user) {
        String token="";
        token= JWT.create().withAudience(Integer.toString(user.getId()))  // 将 user id 保存到 token 里面
                .sign(Algorithm.HMAC256(user.getPassword()));// 以 password 作为 token 的密钥
        return token;
    }

    public String getToken(String openId, String sessionKey) {
        String token="";
        token= JWT.create().withAudience(openId)
                .sign(Algorithm.HMAC256(openId));
        return token;
    }
}
