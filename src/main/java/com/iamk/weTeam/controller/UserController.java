package com.iamk.weTeam.controller;


import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.UnicomRuntimeException;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.*;
import com.iamk.weTeam.model.entity.*;
import com.iamk.weTeam.model.vo.IUserVo;
import com.iamk.weTeam.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    AwardRepository awardRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    UserAttentionRepository userAttentionRepository;
    @Resource
    AdvantageRepository advantageRepository;
    @Resource
    AcademyRepository academyRepository;
    @Resource
    AdminRepository adminRepository;
    @Autowired
    RedisUtil redisUtil;



    /**
     * 查找showMe = 1 的用户
     * @param currentPage
     * @param pageSize
     * @return
     */
    // @PassToken
    // @GetMapping("/showUsers")
    // public ResultUtil showUsers(@RequestParam(defaultValue = "1") Integer currentPage,
    //                             @RequestParam(defaultValue = "10") Integer pageSize){
    //     Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "userViews"));
    //     List<IUserVo> users = userRepository.findByShowMe(1, pageable);
    //     return ResultUtil.success(users);
    // }

    /**
     * 查找showMe = 1 的用户 + 筛选
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PassToken
    @PostMapping(value= "/users")
    public ResponseEntity games(@RequestParam(defaultValue = "") String key,
                                @RequestParam(defaultValue = "") String tag,
                                @RequestParam(defaultValue = "") String academy,
                                @RequestParam(defaultValue = "") String grade,
                                @RequestParam(defaultValue = "") String sort,
                                @RequestParam(defaultValue = "1") Integer currentPage,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        System.out.println("academy: " + academy);
        System.out.println("grade: " + grade);
        System.out.println("sort: " + sort);
        System.out.println("curr: " + currentPage);
        System.out.println("size: " + pageSize);
        // showMe = 1
        Specification spec = SpecificationFactory.equal("showMe", 1);
        // name
        if(StringUtils.isNotBlank(key)){
            spec = spec.and(SpecificationFactory.containsLike("nickname", key));
        }
        // grade
        if(StringUtils.isNotBlank(grade) && !"[]".equals(grade)){
            String[] grades = MyUtils.toStringArray(grade);
            for (String s:grades
                 ) {
                System.out.println(s + " ");
            }
            spec = spec.and(SpecificationFactory.in("grade", grades));
        }
        // academy
        if(StringUtils.isNotBlank(academy) && !"[]".equals(academy)){
            Integer[] academies = MyUtils.toIntegerArray(academy);
            spec = spec.and(SpecificationFactory.in("academyId", academies));
        }
        // time
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "userViews"));
        // if(StringUtils.isNotBlank(sort)){
        //     switch (sort) {
        //         // 访问量
        //         case "1":
        //             break;
        //         // 点赞量
        //         case "2":
        //             break;
        //     }
        // }

        /*
         * 一个待解决的bug,同gameController
         */
        List<User> users = userRepository.findAll(spec, pageable);
        // 设置学院信息
        List<Academy> academies = academyRepository.findAll();
        for (User u : users) {
            Integer academyId = u.getAcademyId();
            u.setAcademy(academies.get(academyId-1).getName());
        }


        // Page<User> users = userRepository.findAll(spec, pageable);
        // List<UserVo> g = new ArrayList<UserVo>();
        // g.addAll(users);

        // tag
        // if(StringUtils.isNotBlank(tag) && !"[]".equals(tag)) {
        //     Integer[] tags = MyUtils.toIntegerArray(tag);
        //     for (Integer t : tags ) {
        //         GameTag gt = gameTagRepository.findById(t).orElse(null);
        //         int len = g.size();
        //         for (int i = 0; i < len; i++) {
        //             Set<GameTag> gameTags = g.get(i).getGameTags();
        //             if(gameTags.size() == 0){
        //                 g.remove(i);
        //                 len--;
        //                 i--;
        //                 continue;
        //             }
        //             if(gt!=null && !gameTags.contains(gt)){
        //                 g.remove(i);
        //                 len--;
        //                 i--;
        //             }
        //         }
        //     }
        // }
        return ResponseEntity.ok(ResultUtil.success(users));
    }


    /**
     * 查找用户
     * @param id 用户id
     * @return User
     */
    @GetMapping("/{id}")
    public ResultUtil findUser(@PathVariable Integer id){
        // redis中取
        String key = RedisKeyUtil.createKey("user", "userId", id);
        if(redisUtil.hasKey(key)){
            Object o = redisUtil.get(key);
            return ResultUtil.success(o);
        }
        // System.out.println("从sql中取");
        // sql中取
        User user = null;
        try {
            user = userRepository.findById(id).orElse(null);
            // null
            if (user == null){
                return ResultUtil.error(UnicomResponseEnums.NO_USER_EXIST);
            }
            // academy
            if(user.getAcademyId()!=null) {
                Academy academy = academyRepository.findById(user.getAcademyId()).orElse(null);
                if(academy != null){
                    user.setAcademy(academy.getName());
                }
            }
            // userType
            Admin admin = adminRepository.findByUserId(id);
            if(admin!=null) {
                user.setUserType(admin.getUserType());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        // 存到redis 1H
        redisUtil.set(key, user, 60*60);
        return ResultUtil.success(user);
    }

    /**
     * 修改用户信息
     * @param id    用户id
     * @return  ResponseEntity
     */
    @PutMapping("/update/{id}")
    public ResultUtil updateUser(@PathVariable Integer id,
                                 @RequestBody User user){
        try {
            User u = userRepository.findById(id).orElse(null);
            if(u != null){
                System.out.println(u);
                UpdateTool.copyNullProperties(u, user);
                userRepository.save(user);

                // 删除redis缓存
                String key = RedisKeyUtil.createKey("user", "userId", id);
                if(redisUtil.hasKey(key)){
                    redisUtil.del(key);
                }
                return ResultUtil.success(UnicomResponseEnums.UPDATE_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.error(UnicomResponseEnums.UPDATE_FAIL);
    }

    /**
     * 关注列表
     * @param id    查看的用户id
     * @return
     */
    @GetMapping("/attentionsList/{id}")
    public ResponseEntity getAttentions(@PathVariable Integer id, HttpServletRequest httpServletRequest,
                                        @RequestParam(defaultValue = "0") Integer currUseId) {
        JSONObject jsonObject = new JSONObject();
        try {
            // 这个id关注了谁
            List<Integer> attentionIds = userAttentionRepository.findByUserId(id);
            System.out.println(attentionIds);
            List<User> attentions = userRepository.findByIdIn(attentionIds);
            // 当前登录用户的关注列表
            if(currUseId == 0){
                String token = httpServletRequest.getHeader("token");
                currUseId = MyUtils.getUserIdFromToken(token);
            }
            List<Integer> ids = userAttentionRepository.findByUserId(currUseId);
            jsonObject.put("attentionIds", ids);
            jsonObject.put("attentions", attentions);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.ok(ResultUtil.success(jsonObject));
    }


    /**
     * fans列表
     * @param id 查询的id
     * @param httpServletRequest
     * @param currUseId 当前登录的用户id
     * @return
     */
    @GetMapping("/fansList/{id}")
    public ResponseEntity getFans(@PathVariable Integer id, HttpServletRequest httpServletRequest,
                                  @RequestParam(defaultValue = "0") Integer currUseId) {
        JSONObject jsonObject = new JSONObject();
        try {
            // 谁关注了这个id
            List<Integer> fanIds = userAttentionRepository.findByAttentionId(id);
            List<User> fans = userRepository.findByIdIn(fanIds);
            // 当前登录用户的关注列表
            if(currUseId == 0){
                String token = httpServletRequest.getHeader("token");
                currUseId = MyUtils.getUserIdFromToken(token);
            }
            List<Integer> ids = userAttentionRepository.findByUserId(currUseId);
            jsonObject.put("attentionIds", ids);
            jsonObject.put("fans", fans);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(ResultUtil.success(jsonObject));
    }


    /**
     * 关注总数
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/countAttentions")
    public ResultUtil countAttentions(@RequestParam(defaultValue = "0") Integer id,
                                      HttpServletRequest httpServletRequest){
        int num = 0;
        try {
            if(id==0){
                String token = httpServletRequest.getHeader("token");
                id = MyUtils.getUserIdFromToken(token);
            }
            num = userAttentionRepository.countByUserId(id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(num);
    }

    /**
     * 粉丝总数
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/countFans")
    public ResultUtil countFans(@RequestParam(defaultValue = "0") Integer id,
                                HttpServletRequest httpServletRequest){
        int num = 0;
        try {
            if(id==0){
                String token = httpServletRequest.getHeader("token");
                id = MyUtils.getUserIdFromToken(token);
            }
            num = userAttentionRepository.countByAttentionId(id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(num);
    }

    /**
     * 关注
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/fan/{id}")
    public ResultUtil fan(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            UserAttention ua = new UserAttention();
            ua.setUserId(userId);
            ua.setAttentionId(id);
            userAttentionRepository.save(ua);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }


    /**
     * 取消关注
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/unFan/{id}")
    public ResultUtil unFan(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            UserAttention ua = new UserAttention();
            ua.setUserId(userId);
            ua.setAttentionId(id);
            userAttentionRepository.delete(ua);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }


    /**
     * 获取特长列表
     * @param id
     * @return
     */
    @RequestMapping("/getAdvantage/{id}")
    public ResultUtil getAdvantage(@PathVariable Integer id) {

        List<Advantage> list = advantageRepository.findAllByUserId(id);
        return ResultUtil.success(list);
    }


    /**
     * 添加特长
     * @param advantage
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/addAdvantage")
    public ResultUtil addAdvantage(@RequestBody Advantage advantage, HttpServletRequest httpServletRequest) {
        try {
            // userId
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            // advantage
            advantage.setUserId(userId);
            Advantage adv = advantageRepository.save(advantage);
            System.out.println(adv.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 删除特长
     * @param id
     * @return
     */
    @RequestMapping("/delAdvantage/{id}")
    public ResultUtil delAdvantage(@PathVariable Integer id) {
        advantageRepository.deleteById(id);
        return ResultUtil.success();
    }

    /**
     * 修改特长
     * @param id
     * @param advantage
     * @return
     */
    @RequestMapping("/editAdvantage/{id}")
    public ResultUtil editAdvantage(@PathVariable Integer id, @RequestBody Advantage advantage) {
        try {
            Advantage ad = advantageRepository.findById(id).orElse(null);
            if(ad != null){
                ad.setName(advantage.getName());
                ad.setBrief(advantage.getBrief());
            }
            advantageRepository.save(ad);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }



    /**
     * 获奖经历列表
     * @param id
     * @return
     */
    @RequestMapping("/getAward/{id}")
    public ResultUtil getAward(@PathVariable Integer id) {

        List<Award> list = awardRepository.findAllByUserId(id);
        System.out.println(list);
        return ResultUtil.success(list);
    }


    /**
     * 添加获奖经历
     * @param award
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/addAward")
    public ResultUtil addAward(@RequestBody Award award, HttpServletRequest httpServletRequest) {
        try {
            System.out.println(award);
            // userId
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            // advantage
            award.setUserId(userId);
            Award adv = awardRepository.save(award);
            System.out.println(adv.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 删除获奖经历
     * @param id
     * @return
     */
    @RequestMapping("/delAward/{id}")
    public ResultUtil delAward(@PathVariable Integer id) {
        awardRepository.deleteById(id);
        return ResultUtil.success();
    }

    /**
     * 修改获奖经历
     * @param id
     * @param award
     * @return
     */
    @RequestMapping("/editAward/{id}")
    public ResultUtil editAward(@PathVariable Integer id, @RequestBody Award award) {
        try {
            Award ad = awardRepository.findById(id).orElse(null);
            if(ad != null){
                ad.setName(award.getName());
                ad.setBrief(award.getBrief());
                ad.setType(award.getType());
            }
            awardRepository.save(ad);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

}

