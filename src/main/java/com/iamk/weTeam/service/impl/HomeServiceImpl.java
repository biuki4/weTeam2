package com.iamk.weTeam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.config.MiniProgramConfig;
import com.iamk.weTeam.common.utils.MyUtils;
import com.iamk.weTeam.model.entity.UnionUser;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.UnionUserRepository;
import com.iamk.weTeam.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class HomeServiceImpl implements HomeService {

    @Autowired
    MiniProgramConfig miniProgramConfig;
    @Resource
    UnionUserRepository unionUserRepository;

    @Async
    @Override
    public void update(User user, JSONObject jsonObject) {
        log.info("async update unionUser: " + user.getId());
        JSONObject school_account = (JSONObject) JSONObject.toJSON(jsonObject.get("school_account"));

        UnionUser unionUser = new UnionUser();
        unionUser.setUserId(user.getId());
        unionUser.setName((String) school_account.get("name"));

        int isBoundWX = (boolean) jsonObject.get("bound_wechatOfficialAccount") ? 1 : 0;
        unionUser.setBoundWechat(isBoundWX);

        int isBoundNPU = (boolean)jsonObject.get("bound_schoolAccount") ? 1 : 0;
        unionUser.setBoundSchoolAccount(isBoundNPU);

        int type = 0;
        String typeStr = (String) school_account.get("type");
        if("学生".equals(typeStr) || "本科生".equals(typeStr)) type = 1;
        if("研究生".equals(typeStr)) type = 2;
        if("博士生".equals(typeStr)) type = 3;
        unionUser.setType(type);

        unionUserRepository.save(unionUser);
    }
}
