package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.Enum.TeamEnum;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.constant.UnionConstant;
import com.iamk.weTeam.common.utils.MyUtils;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.ActivityTeam;
import com.iamk.weTeam.model.entity.Game;
import com.iamk.weTeam.model.entity.Team;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.ActivityTeamRepository;
import com.iamk.weTeam.repository.UserRepository;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * 活动组队
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityTeamController {
    @Resource
    UserRepository userRepository;
    @Resource
    ActivityTeamRepository activityTeamRepository;


    /**
     * 创建队伍
     * @param team team
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @PostMapping("/create")
    public ResultUtil create(@RequestBody Team team, HttpServletRequest httpServletRequest) {

        return ResultUtil.success();
    }
}
