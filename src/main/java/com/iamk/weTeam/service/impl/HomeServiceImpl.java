package com.iamk.weTeam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.config.MiniProgramConfig;
import com.iamk.weTeam.common.utils.MyUtils;
import com.iamk.weTeam.model.entity.Academy;
import com.iamk.weTeam.model.entity.UnionUser;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.AcademyRepository;
import com.iamk.weTeam.repository.UnionUserRepository;
import com.iamk.weTeam.repository.UserRepository;
import com.iamk.weTeam.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
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
    @Resource
    UserRepository userRepository;
    @Resource
    AcademyRepository academyRepository;

    @Async
    @Override
    public void update(User user, JSONObject jsonObject) {
        log.info("-----async update user info:-----");
        log.info(user.toString());
        log.info(jsonObject.toJSONString());
        // 不是i瓜大用户
        if(!StringUtils.isNotBlank((String) jsonObject.get("name"))) {
            log.info("-----not union user-----");
            return;
        }
        // 是i瓜大用户
        if((boolean)jsonObject.get("bound_schoolAccount")) {
            JSONObject school_account = (JSONObject) JSONObject.toJSON(jsonObject.get("school_account"));
            log.info(String.valueOf(school_account));

            // 更新user表
            user = userRepository.findById(user.getId()).orElse(null);
            if(user!=null) {
                String school_id = (String) school_account.get("school_id");
                user.setUsername(school_id);
                user.setGrade(school_id.substring(0, 4));

                // academy
                Academy a = academyRepository.findByName(school_account.get("college"));
                if(a!=null) {
                    user.setAcademyId(a.getId());
                }
                userRepository.save(user);
                log.info(user.toString());
                log.info("----finish update user----");

                // 更新union_user表
                UnionUser unionUser = unionUserRepository.findByUserId(user.getId());
                if(unionUser==null) {
                    unionUser = new UnionUser();
                    unionUser.setUserId(user.getId());
                    unionUser.setName((String) school_account.get("name"));
                    unionUser.setType((String) school_account.get("type"));
                }
                int isBoundWX = (boolean) jsonObject.get("bound_wechatOfficialAccount") ? 1 : 0;
                unionUser.setBoundWechat(isBoundWX);

                int isBoundNPU = (boolean)jsonObject.get("bound_schoolAccount") ? 1 : 0;
                unionUser.setBoundSchoolAccount(isBoundNPU);
                unionUserRepository.save(unionUser);
                log.info(unionUser.toString());
                log.info("----finish update unionUser----");
            }
        }
        log.info("--------------async update user info end-----------------");
    }
}
