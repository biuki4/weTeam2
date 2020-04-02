package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.GameCategory;
import com.iamk.weTeam.repository.GameCategoryRepository;
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

    /**
     * 所有分类
     * @return
     */
    @PassToken
    @GetMapping
    public ResultUtil category() {
        List<GameCategory> categories = gameCategoryRepository.findAll();
        return ResultUtil.success(categories);
    }


}
