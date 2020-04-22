package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.RedisUtil;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.GameCategory;
import com.iamk.weTeam.repository.GameCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class GameCategoryController {

    @Resource
    GameCategoryRepository gameCategoryRepository;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 所有分类
     * @return
     */
    @PassToken
    @GetMapping
    public ResultUtil category() {
        // redis中取
        String key = "category";
        if(redisUtil.hasKey(key)){
            Object o = redisUtil.get(key);
            return ResultUtil.success(o);
        }

        List<GameCategory> categories = gameCategoryRepository.findAll();
        // 存到redis 1h
        redisUtil.set(key, categories, 60*60);
        return ResultUtil.success(categories);
    }


}
