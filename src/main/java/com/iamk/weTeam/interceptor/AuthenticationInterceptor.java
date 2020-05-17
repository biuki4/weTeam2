package com.iamk.weTeam.interceptor;

import com.iamk.weTeam.common.Enum.UnicomResponseEnums;
import com.iamk.weTeam.common.expection.UnicomRuntimeException;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.JwtUtils;
import com.iamk.weTeam.common.utils.RedisKeyUtil;
import com.iamk.weTeam.common.utils.RedisUtil;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/***
 * 拦截请求并验证token
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RedisUtil redisUtil;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse, Object object) throws Exception {

        log.info("当前访问： " + httpServletRequest.getRequestURI());
        System.out.println("当前访问： " + httpServletRequest.getRequestURI());
        String requestURI = httpServletRequest.getRequestURI();
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passToken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required())
                return true;
        }
        // System.out.println("没有passToken");
        // 从 http 请求头中取出 token
        String token = httpServletRequest.getHeader("token");
        // System.out.println("拦截器： " + token);
        // token为空
        if (token == null || "".equals(token)) {
            System.out.println("没有token");
            throw new UnicomRuntimeException(UnicomResponseEnums.SIGNATURE_NOT_MATCH);
        }
        // 解析token获取用户信息
        Claims claims = JwtUtils.validateJWT2(token);
        // 解析错误
        if(claims == null){
            System.out.println("claims解析错误");
            throw new UnicomRuntimeException(UnicomResponseEnums.SIGNATURE_NOT_MATCH);
        }
        // System.out.println("claims: " + claims);
        String userId = (String) claims.get("jti");
        String openId = (String) claims.get("sub");

        // 从redis中查看登录是否过期
        String key = RedisKeyUtil.createKey("login:user", "userId", userId);
        // System.out.println("key: " + key);
        if(!redisUtil.hasKey(key)){
            throw new UnicomRuntimeException(UnicomResponseEnums.LOGIN_DATED);
        }

        User user = userRepository.findById(Integer.parseInt(userId)).orElse(null);
        if(user == null || !openId.equals(user.getOpenId())) {
            throw new UnicomRuntimeException(UnicomResponseEnums.SIGNATURE_NOT_MATCH);
        }
        System.out.println("token验证通过！");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
