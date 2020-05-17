package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.utils.MyUtils;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.Found;
import com.iamk.weTeam.repository.FoundRepository;
import com.iamk.weTeam.service.FoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("api/found")
public class FoundController {

    @Resource
    FoundRepository foundRepository;
    @Autowired
    FoundService foundService;

    @RequestMapping("/found")
    public ResultUtil found(@RequestBody Found found, HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        found.setUserId(userId);
        found.setCreateTime(new Date());
        foundRepository.save(found);
        foundService.found(found);
        return ResultUtil.success();
    }
}
