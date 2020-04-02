package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.MyUtils;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.Admin;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.model.vo.IUserVo;
import com.iamk.weTeam.repository.AdminRepository;
import com.iamk.weTeam.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    @Resource
    UserRepository userRepository;
    @Resource
    AdminRepository adminRepository;

    /**
     * 添加管理员
     * @param username
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @GetMapping("/add")
    public ResultUtil showUsers(@RequestParam String username, HttpServletRequest httpServletRequest){
        // 判断该用户有无权限
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ResultUtil.error(UnicomResponseEnums.NO_PERMISSION);
        }
        int userType = user.getUserType();
        if(userType > 1){
            return ResultUtil.error(UnicomResponseEnums.NO_PERMISSION);
        }
        // 添加admin
        User u = userRepository.findByUsername(username);
        int uType = u.getUserType();
        if(u == null){
            return ResultUtil.error(UnicomResponseEnums.NO_USER_EXIST);
        }
        if(userType <= uType){
            return ResultUtil.error(UnicomResponseEnums.NO_PERMISSION);
        }
        if(uType == 3){
            u.setUserType(2);
            userRepository.save(u);
            Admin ad = new Admin();
            ad.setUserId(u.getId());
            ad.setSetId(userId);
            adminRepository.save(ad);
            return ResultUtil.success();
        }
        return ResultUtil.error(UnicomResponseEnums.NO_PERMISSION);
    }

    @GetMapping("/myAdmin")
    public ResultUtil findMessages(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        List<Map<String, Object>> bySetId = adminRepository.findBySetId(userId);
        System.out.println(bySetId);
        return ResultUtil.success(bySetId);
    }

}
