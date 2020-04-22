package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.RedisUtil;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.Academy;
import com.iamk.weTeam.repository.AcademyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/academy")
public class AcademyController {

    @Resource
    AcademyRepository gameSourceRepository;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 查询所有学院
     * @return
     */
    @PassToken
    @GetMapping
    public ResultUtil academy(){
        // redis中取
        String key = "academies";
        if(redisUtil.hasKey(key)){
            Object o = redisUtil.get(key);
            return ResultUtil.success(o);
        }
        List<Academy> academies = gameSourceRepository.findAll();
        // // 存到redis 1h
        redisUtil.set(key, academies, 60*60);
        return ResultUtil.success(academies);
    }
}
