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
import java.util.UUID;

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
        // team.setSize(game.getTeamSize());
        // String teamNo = MyUtils.createTeamNo(team);
        team.setTeamNo(String.valueOf(UUID.randomUUID()));
        // System.out.println(teamNo);
        // System.out.println(team);
        teamRepository.save(team);
        return ResultUtil.success();
    }

    /**
     * 解散队伍
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @RequestMapping("/dissolve/{id}")
    public ResultUtil dissolve(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        Team team = teamRepository.findById(id).orElse(null);
        // 非队长操作
        if(team.getUserId() != userId) {
            return ResultUtil.error(UnicomResponseEnums.NOT_LEADER);
        }
        // 删除
        teamUserRepository.deleteByTeamId(id);
        teamRepository.delete(team);
        return ResultUtil.success();
    }

    /**
     * 更新队伍信息
     * @param teamId
     * @param name
     * @param brief
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/update")
    public ResultUtil update(@RequestParam Integer teamId,
                             @RequestParam(defaultValue = "") String name,
                             @RequestParam(defaultValue = "") String contact,
                             @RequestParam(defaultValue = "") String size,
                             @RequestParam(defaultValue = "") String brief,
                             HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        Team team = teamRepository.findById(teamId).orElse(null);
        // 非队长操作
        if(team.getUserId() != userId) {
            return ResultUtil.error(UnicomResponseEnums.NOT_LEADER);
        }
        // 更新
        if(!name.equals("")){
            team.setName(name);
        }
        if(!brief.equals("")){
            team.setBrief(brief);
        }
        if(!size.equals("")){
            team.setSize(Integer.parseInt(size));
        }
        if(!contact.equals("")) {
            team.setContact(contact);
        }
        teamRepository.save(team);
        return ResultUtil.success();
    }


    /**
     * 组队列表
     * @param id    竞赛id
     * @return
     */
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
     * 正在申请的用户列表
     * @param id teamid
     * @return
     */
    @RequestMapping("applyList/{id}")
    public ResultUtil applyList(@PathVariable Integer id) {
        // 申请成员
        List<Map<String, Object>> users = teamRepository.findTeamMember(id, 0);
        return ResultUtil.success(users);
    }

    /**
     * 队伍详情
     * @param id teamId
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/teamDetail/{id}")
    public ResultUtil teamDetail(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        // team
        Team team = teamRepository.findById(id).orElse(null);
        // game
        Game game = gameRepository.findById(team.getGameId()).orElse(null);
        // leader
        User leader = userRepository.findById(team.getUserId()).orElse(null);
        // 正式成员
        List<Map<String, Object>> members = teamRepository.findTeamMember(id, 1);
        team.setMembers(members);
        // 该userId是否是正式成员
        for(Map<String, Object> m : members){
            if(m.get("id") == userId){
                team.setIsMember(true);
                break;
            }
        }
        // 申请成员
        List<Map<String, Object>> applicant = teamRepository.findTeamMember(id, 0);
        team.setApplicant(applicant);
        // 该userId是否是申请成员
        if(!team.getIsMember()){
            for(Map<String, Object> a : applicant){
                if(a.get("id") == userId){
                    team.setIsApplicant(true);
                    break;
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("team", team);
        jsonObject.put("game", game);
        jsonObject.put("leader",leader);
        return ResultUtil.success(jsonObject);
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
        if(num >= team.getSize()) {
            return ResultUtil.error(UnicomResponseEnums.TEAM_FULL);
        }
        // 已申请
        TeamUser t = teamUserRepository.findByTeamIdAndUserId(team.getId(), userId);
        if(t != null){
            return ResultUtil.error(UnicomResponseEnums.HAS_APPLY);
        }
        // 已加入别的队伍
        Integer integer = teamUserRepository.countUser(team.getGameId(), userId, 1);
        if(integer > 0){
            return ResultUtil.error(UnicomResponseEnums.HAS_TEAM);
        }
        // 该Id已经创建队伍
        Team t1 = teamRepository.findByUserIdAndGameId(userId, team.getGameId());
        if(t1 != null){
            return ResultUtil.error(UnicomResponseEnums.HAS_TEAM);
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
    public ResultUtil agree(@RequestParam Integer teamId,
                            @RequestParam Integer applyId, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        Team team = teamRepository.findById(teamId).orElse(null);
        // 非队长操作
        if(team.getUserId() != userId) {
            return ResultUtil.error(UnicomResponseEnums.NOT_LEADER);
        }
        // 队伍已满
        Integer num = teamUserRepository.countTeamMember(team.getId(), 1);
        if(num >= team.getSize()) {
            return ResultUtil.error(UnicomResponseEnums.TEAM_FULL);
        }
        // 已加入别的队伍
        Integer integer = teamUserRepository.countUser(team.getGameId(), applyId, 1);
        if(integer > 0){
            return ResultUtil.error(UnicomResponseEnums.HAS_TEAM);
        }
        // 该Id已经创建队伍
        Team t1 = teamRepository.findByUserIdAndGameId(applyId, team.getGameId());
        if(t1 != null){
            return ResultUtil.error(UnicomResponseEnums.HAS_TEAM);
        }
        // 同意加入
        TeamUser tu = teamUserRepository.findByTeamIdAndUserId(teamId, applyId);
        teamUserRepository.delete(tu);
        tu.setType(1);
        teamUserRepository.save(tu);
        return ResultUtil.success();
    }

    /**
     * 拒绝申请
     * @param teamId
     * @param applyId
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/disagree")
    public ResultUtil disagree(@RequestParam Integer teamId,
                            @RequestParam Integer applyId, HttpServletRequest httpServletRequest) {
        // userId
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        Team team = teamRepository.findById(teamId).orElse(null);
        // 非队长操作
        if(team.getUserId() != userId) {
            return ResultUtil.error(UnicomResponseEnums.NOT_LEADER);
        }
        // 拒绝
        TeamUser tu = teamUserRepository.findByTeamIdAndUserId(teamId, applyId);
        teamUserRepository.delete(tu);
        tu.setType(2);
        teamUserRepository.save(tu);
        return ResultUtil.success();
    }

    /**
     * 删除一个队员
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
        // 删除
        TeamUser tu = teamUserRepository.findByTeamIdAndUserId(teamId, applyId);
        teamUserRepository.delete(tu);
        return ResultUtil.success();
    }

    /**
     * 我的队伍
     * @param id userId
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/myTeam")
    public ResultUtil myTeam(@RequestParam(defaultValue = "0") Integer id, HttpServletRequest httpServletRequest){
        if(id==0){
            String token = httpServletRequest.getHeader("token");
            id = MyUtils.getUserIdFromToken(token);
        }
        // 我的创建
        List<Map<String, Object>> myTeams = teamRepository.findByUserId(id);
        // 我的参与
        List<Map<String, Object>> myParticipate = teamRepository.findParticipateById(id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("myTeams", myTeams);
        jsonObject.put("myParticipate", myParticipate);
        return ResultUtil.success(jsonObject);
    }

}
