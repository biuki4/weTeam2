package com.iamk.weTeam.controller;

import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.Enum.ActivityEnum;
import com.iamk.weTeam.common.Enum.AdminEnum;
import com.iamk.weTeam.common.Enum.GameEnum;
import com.iamk.weTeam.common.Enum.UnicomResponseEnums;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.*;
import com.iamk.weTeam.model.dto.ActivityDTO;
import com.iamk.weTeam.model.entity.*;
import com.iamk.weTeam.model.entity.Activity;
import com.iamk.weTeam.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Resource
    ActivityRepository activityRepository;
    @Resource
    ActivityAttentionRepository activityAttentionRepository;
    @Resource
    ActivityCategoryRepository activityCategoryRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    AdminRepository adminRepository;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 轮播图数据
     * @return
     */
    @PassToken
    @GetMapping("/getSlideshow")
    public ResultUtil getSlideshow(){
        // redis中取
        String key = "activitySlideshow";
        if(redisUtil.hasKey(key)){
            Object o = redisUtil.get(key);
            System.out.println(o);
            return ResultUtil.success(o);
        }
        System.out.println("sql...");
        List<Map<String, Object>> slideshow = activityRepository.findSlideshow();
        // 存到redis 1h
        redisUtil.set(key, slideshow, 60*60);
        return ResultUtil.success(slideshow);
    }


    /**
     * 多条件复杂查询活动
     * @param activityDTO
     * @param currentPage
     * @param pageSize
     * @return
     * @throws ParseException
     */
    @PassToken
    @RequestMapping("/activities")
    public ResultUtil activities(ActivityDTO activityDTO,
                                 @RequestParam(defaultValue = "1") Integer currentPage,
                                 @RequestParam(defaultValue = "10") Integer pageSize) throws ParseException {

        System.out.println(activityDTO);
        System.out.println(currentPage);
        System.out.println(pageSize);

        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "postTime"));
        // Name
        Specification spec = SpecificationFactory.containsLike("activityName", activityDTO.getKey());
        // source
        // if(StringUtils.isNotBlank(activityDTO.getSource()) && !"[]".equals(activityDTO.getSource())){
        //     String[] sources = MyUtils.toStringArray(activityDTO.getSource());
        //     spec = spec.and(SpecificationFactory.in("activitySource", sources));
        // }
        // category
        if(activityDTO.getCategoryId()!=null){
            spec = spec.and(SpecificationFactory.join_equal("activityCategory", "id", activityDTO.getCategoryId()));
        }
        // time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse1 = sdf.parse(sdf.format(new Date()));
        Integer time = activityDTO.getTime() == null?0:activityDTO.getTime();
        switch (time) {
            // 正在进行
            case 5:
                spec = spec.and(SpecificationFactory.greaterEqualThan("registerEndTime", parse1));
                break;
            // 已结束
            case 6:
                spec = spec.and(SpecificationFactory.lessThan("registerEndTime", parse1));
                break;
            // 全部
            case 7:
                break;
            default:
                spec = spec.and(SpecificationFactory.greaterEqualThan("registerEndTime", parse1));
        }

        List<Activity> all = activityRepository.findAll(spec, pageable);

        return ResultUtil.success(all);
    }

    /**
     * 根据id查找活动
     * @param id
     * @return
     */
    @PassToken
    @RequestMapping("/{id}")
    public ResultUtil findById(@PathVariable int id, HttpServletRequest httpServletRequest) {

        JSONObject jsonObject = new JSONObject();
        try {
            // game
            Activity activity = activityRepository.findById(id).orElse(null);
            if(activity==null) {
                return ResultUtil.error(ActivityEnum.ACTIVITY_NOT_EXIT);
            }
            // poster
            User poster = userRepository.findById(activity.getPostId()).orElse(null);
            // isCollect
            String token = httpServletRequest.getHeader("token");
            jsonObject.put("isCollect", false);
            if(token!=null && !"".equals(token)){
                Integer userId = MyUtils.getUserIdFromToken(token);
                ActivityAttention ga = activityAttentionRepository.findByUserIdAndActivityId(userId, id);
                if(ga != null){
                    jsonObject.put("isCollect", true);
                }
            }
            jsonObject.put("activity", activity);
            jsonObject.put("poster", poster);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(jsonObject);
    }

    /**
     * 查询收藏的活动
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/attentionList")
    public ResultUtil findGameAttention(@RequestParam(defaultValue = "0") Integer id, HttpServletRequest httpServletRequest) {
        JSONObject jsonObject = new JSONObject();
        try{
            if(id==0){
                String token = httpServletRequest.getHeader("token");
                id = MyUtils.getUserIdFromToken(token);
            }
            List<Integer> ids = activityAttentionRepository.findActivityIdByUserId(id);
            // 正在进行
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date parse1 = sdf.parse(sdf.format(new Date()));
            List<Activity> activity = activityRepository.findAllByIdInAndRegisterEndTimeGreaterThanEqualOrderByPostTimeDesc(ids, parse1);
            // 已经结束
            List<Activity> activity2 = activityRepository.findAllByIdInAndRegisterEndTimeLessThanOrderByPostTimeDesc(ids, parse1);
            jsonObject.put("curActivity", activity);
            jsonObject.put("endActivity", activity2);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(jsonObject);
    }

    /**
     * 收藏活动
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/collect/{id}")
    public ResultUtil collect(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        try{
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            ActivityAttention ga = new ActivityAttention();
            ga.setActivityId(id);
            ga.setUserId(userId);
            activityAttentionRepository.save(ga);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 取消收藏
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/unCollect/{id}")
    public ResultUtil unCollect(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        try{
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            ActivityAttention ga = new ActivityAttention();
            ga.setActivityId(id);
            ga.setUserId(userId);
            activityAttentionRepository.delete(ga);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }


    /**
     * 发布活动
     * @param
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/add")
    public ResultUtil addGame(@RequestBody Activity activity, HttpServletRequest httpServletRequest){
        System.out.println("发布活动啦~");
        String token = httpServletRequest.getHeader("token");
        Integer postId = MyUtils.getUserIdFromToken(token);
        // 非管理员管理员
        Admin admin = adminRepository.findByUserId(postId);
        if(admin==null){
            return ResultUtil.error(AdminEnum.Admin_NOT_ADMIN);
        }
        // poster
        try{
            activity.setPostId(postId);
            activity.setPostTime(new Date());
            activity.setModifiedTime(new Date());
            activity.setActivityViews(0);
            activity = activityRepository.save(activity);
            // System.out.println("发送完成");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(activity);
    }

    /**
     * 编辑竞赛
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/update")
    public ResultUtil updateGame(@RequestBody Activity activity, HttpServletRequest httpServletRequest){
        try{
            Activity old_activity = activityRepository.findById(activity.getId()).orElse(null);
            // e is null
            if(old_activity==null) {
                return ResultUtil.error(ActivityEnum.ACTIVITY_NOT_EXIT);
            }

            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            // 非管理员
            Admin admin = adminRepository.findByUserId(userId);
            if(admin==null){
                return ResultUtil.error(AdminEnum.Admin_NOT_ADMIN);
            }
            // 权限不足
            if(admin.getUserType() > 0 && activity.getPostId() != userId) {
                return ResultUtil.error(AdminEnum.ADMIN_NO_RIGHT);
            }
            // 更新game
            UpdateTool.copyNullProperties(old_activity, activity);
            activityRepository.save(activity);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(activity);
    }

    /**
     * 删除活动
     * @param id 竞赛id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/delete/{id}")
    public ResultUtil delete(@PathVariable Integer id, HttpServletRequest httpServletRequest){
        try{
            Activity activity = activityRepository.findById(id).orElse(null);
            // is null
            if(activity==null) {
                return ResultUtil.error(GameEnum.GAME_NOT_EXIT);
            }
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            // 非管理员
            Admin admin = adminRepository.findByUserId(userId);
            if(admin==null){
                return ResultUtil.error(AdminEnum.Admin_NOT_ADMIN);
            }
            // 权限不足
            if(admin.getUserType() > 0 && !activity.getPostId().equals(userId)) {
                return ResultUtil.error(AdminEnum.ADMIN_NO_RIGHT);
            }
            activityRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 活动分类
     * @return
     */
    @PassToken
    @RequestMapping("/category")
    public ResultUtil category() {
        // System.out.println("123123123");
        // redis中取
        // String key = "category_activity";
        // if(redisUtil.hasKey(key)){
        //     Object o = redisUtil.get(key);
        //     return ResultUtil.success(o);
        // }
        List<ActivityCategory> categories = activityCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "rank"));
        // 存到redis 1h
        // redisUtil.set(key, categories, 60*60);
        return ResultUtil.success(categories);
    }

}
