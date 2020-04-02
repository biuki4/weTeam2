package com.iamk.weTeam.controller;

import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.DateUtil;
import com.iamk.weTeam.common.utils.MyUtils;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.*;
import com.iamk.weTeam.model.vo.IUserVo;
import com.iamk.weTeam.repository.GameRepository;
import com.iamk.weTeam.repository.TeamRepository;
import com.iamk.weTeam.repository.TeamUserRepository;
import com.iamk.weTeam.repository.UserRepository;
import org.hibernate.annotations.Parameter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    @Resource
    TeamRepository teamRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    GameRepository gameRepository;
    @Resource
    TeamUserRepository teamUserRepository;

    /**
     * 创建队伍
     * @param team team
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @PostMapping("/create")
    public ResultUtil create(@RequestBody Team team, HttpServletRequest httpServletRequest) {
        System.out.println(team);
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        // Integer userId=13;
        Game game = gameRepository.findById(team.getGameId()).orElse(null);
        // 已经创建
        Team t = teamRepository.findByUserIdAndGameId(userId, game.getId());
        if(t != null){
            return ResultUtil.error(UnicomResponseEnums.HAS_CREATE);
        }
        // 创建
        // Integer tId = team.getId();
        // Team new_t = new Team();
        // team.setUserId(userId);
        // new_t.setGameId(team.getGameId());
        team.setCreateTime(new Date());
        // new_t.setName(team.getName());
        // new_t.setBrief(team.getBrief());
        team.setSize(game.getTeamSize());
        String teamNo = MyUtils.createTeamNo(team);
        team.setTeamNo(teamNo);
        System.out.println(teamNo);
        System.out.println(team);
        teamRepository.save(team);
        return ResultUtil.success();
    }

    /**
     * 解散队伍
     * @param teamId
     * @param applyId
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @RequestMapping("/dissolve")
    public ResultUtil dissolve(@RequestParam Integer teamId,
                               @RequestParam Integer applyId, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        Team team = teamRepository.findById(teamId).orElse(null);
        // 非队长操作
        if(team.getUserId() != userId) {
            return ResultUtil.error(UnicomResponseEnums.NOT_LEADER);
        }
        // 删除
        teamRepository.delete(team);
        teamUserRepository.deleteByTeamId(team.getId());
        return ResultUtil.success();
    }


    /**
     * 组队列表
     * @param id    竞赛id
     * @return
     */
    // @PassToken
    @RequestMapping("/{id}")
    public ResultUtil findById(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        // Integer userId = 13;
        // team
        List<Team> teams = teamRepository.findByGameId(id, Sort.by(Sort.Direction.DESC, "createTime"));
        for(Team t : teams){
            Integer tId = t.getId();
            // 队长
            User leader = userRepository.findById(t.getUserId()).orElse(null);
            t.setLeader(leader);
            // 正式成员
            List<Map<String, Object>> members = teamRepository.findTeamMember(tId, 1);
            t.setMembers(members);
            // 该userId是否是正式成员
            for(Map<String, Object> m : members){
                if(m.get("id") == userId){
                    t.setIsMember(true);
                    break;
                }
            }
            // 申请成员
            List<Map<String, Object>> applicant = teamRepository.findTeamMember(tId, 0);
            t.setApplicant(applicant);
            // 该userId是否是申请成员
            if(!t.getIsMember()){
                for(Map<String, Object> a : applicant){
                    if(a.get("id") == userId){
                        t.setIsApplicant(true);
                        break;
                    }
                }
            }
        }
        return ResultUtil.success(teams);
    }

    /**
     * 申请加入
     * @param id    teamId
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @RequestMapping("/apply/{id}")
    public ResultUtil apply(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        // Integer userId=13;
        // 队伍已满
        Team team = teamRepository.findById(id).orElse(null);
        Integer num = teamUserRepository.countTeamMember(team.getId(), 1);
        if(num >= team.getSize()-1) {
            return ResultUtil.error(UnicomResponseEnums.TEAM_FULL);
        }
        // 已申请
        TeamUser t = teamUserRepository.findByTeamIdAndUserId(team.getId(), userId);
        if(t != null){
            return ResultUtil.error(UnicomResponseEnums.HAS_APPLY);
        }
        // 队伍未满
        TeamUser tu = new TeamUser();
        tu.setTeamId(id);
        tu.setUserId(userId);
        tu.setType(0);
        teamUserRepository.save(tu);
        return ResultUtil.success();
    }

    /**
     * 取消申请
     * @param id    teamId
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/unApply/{id}")
    public ResultUtil unApply(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        // 取消申请
        teamUserRepository.deleteByTeamIdAndUserId(id, userId);
        return ResultUtil.success();
    }


    /**
     *  同意入队
     * @param teamId
     * @param applyId
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @RequestMapping("/agree")
    public ResultUtil apply(@RequestParam Integer teamId,
                            @RequestParam Integer applyId, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        Team team = teamRepository.findById(teamId).orElse(null);
        // 非队长操作
        if(team.getUserId() != userId) {
            return ResultUtil.error(UnicomResponseEnums.NOT_LEADER);
        }
        // 同意加入
        TeamUser tu = teamUserRepository.findByTeamIdAndUserId(teamId, applyId);
        tu.setType(1);
        teamUserRepository.save(tu);
        return ResultUtil.success();
    }

    /**
     * 删除一个申请 或 一个队员
     * @param teamId
     * @param applyId
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @RequestMapping("/deleteApply")
    public ResultUtil deleteApply(@RequestParam Integer teamId,
                                  @RequestParam Integer applyId, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        Team team = teamRepository.findById(teamId).orElse(null);
        // 非队长操作
        if(team.getUserId() != userId) {
            return ResultUtil.error(UnicomResponseEnums.NOT_LEADER);
        }
        // 同意加入
        TeamUser tu = teamUserRepository.findByTeamIdAndUserId(teamId, applyId);
        teamUserRepository.delete(tu);
        return ResultUtil.success();
    }



}
