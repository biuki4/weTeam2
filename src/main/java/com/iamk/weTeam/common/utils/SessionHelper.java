package com.iamk.weTeam.common.utils;



import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Component
public class SessionHelper {

    @Resource
    UserRepository userRepository;

    public User login(HttpSession session, String userName, String password) {
        if (session.getAttribute("userId") != null) {
            throw new BasicException("已经登录");
        }
        User user = userRepository.findByUsername(userName);
        if (user == null || !EncodeUtil.md5Verify(password, user.getPassword())) {
            throw new BasicException("用户名或密码错误");
        }
        session.setAttribute("userId", user.getId());
        return user;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
