package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.Academy;
import com.iamk.weTeam.repository.AcademyRepository;
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

    /**
     * 查询所有学院
     * @return
     */
    @PassToken
    @GetMapping
    public ResultUtil academy(){
        List<Academy> academies = gameSourceRepository.findAll();
        return ResultUtil.success(academies);
    }
}
