package com.iamk.weTeam.service.impl;

import com.iamk.weTeam.common.config.MiniProgramConfig;
import com.iamk.weTeam.common.utils.DateUtil;
import com.iamk.weTeam.model.entity.AdminApply;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.UserRepository;
import com.iamk.weTeam.service.AdminService;
import com.iamk.weTeam.wxMp.config.WxMpProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@AllArgsConstructor
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    UserRepository userRepository;
    @Autowired
    MiniProgramConfig miniProgramConfig;
    @Autowired
    private WxMpProperties wxMpProperties;

    private final WxMpService wxService;

    @Override
    public void apply(AdminApply adminApply) {
        log.info("管理员申请: " + adminApply);
        WxMpProperties.MpConfig mpConfig = wxMpProperties.getConfigs().get(0);
        // 填充模板
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(wxMpProperties.getMyOpenId())
                .templateId(mpConfig.getInformTemplateId())
                .build();
        /*
         * 模板信息
         */
        templateMessage.addData(new WxMpTemplateData("first", "管理员申请", "#FF0000"));
        templateMessage.addData(new WxMpTemplateData("keyword1", "status: " + adminApply.getStatus(), "#173177"));

        User user = userRepository.findById(adminApply.getUserId()).orElse(null);
        String name = user ==null? "null" :user.getNickname();
        String userName = user ==null? "null" :user.getUsername();
        String contact = adminApply.getContact();
        if (contact == null || "".equals(contact)) {
            contact = user.getContact();
        }
        templateMessage.addData(new WxMpTemplateData("keyword2", "name: " + name, "#173177"));
        templateMessage.addData(new WxMpTemplateData("keyword3", "userName: " + userName, "#173177"));
        templateMessage.addData(new WxMpTemplateData("keyword4", "contact: " + contact, "#173177"));

        String time = DateUtil.format(adminApply.getCreateTime());
        templateMessage.addData(new WxMpTemplateData("keyword5", time, "#173177"));
        templateMessage.addData(new WxMpTemplateData("remark", "remark: " + adminApply.getRemark(), "#173177"));
        try {
            wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (Exception e) {
            log.error("推送失败：" + e.getMessage());
        }
    }
}
