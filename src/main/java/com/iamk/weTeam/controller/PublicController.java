package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.DateUtil;
import com.iamk.weTeam.common.utils.MyUtils;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.common.utils.SpecificationFactory;
import com.iamk.weTeam.model.entity.Activity;
import com.iamk.weTeam.model.entity.Game;
import com.iamk.weTeam.model.vo.ActivityVo;
import com.iamk.weTeam.model.vo.GameVo;
import com.iamk.weTeam.repository.ActivityRepository;
import com.iamk.weTeam.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 对外接口
 */
@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Resource
    GameRepository gameRepository;
    @Resource
    ActivityRepository activityRepository;

    /**
     * 近期竞赛  10 条
     * @return
     */
    @RequestMapping(value= "/games")
    public ResponseEntity games(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            log.info("日期转换错误：" + e.getMessage());
        }
        List<GameVo> games = gameRepository.findTop10ByRegisterEndTimeGreaterThanEqualOrderByPostTimeDesc(date);
        return ResponseEntity.ok(ResultUtil.success(games));
    }

    @RequestMapping(value= "/activities")
    public ResponseEntity activities() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            log.info("日期转换错误：" + e.getMessage());
        }
        List<ActivityVo> activity = activityRepository.findTop10ByRegisterEndTimeGreaterThanEqualOrderByPostTimeDesc(date);
        return ResponseEntity.ok(ResultUtil.success(activity));
    }
}
