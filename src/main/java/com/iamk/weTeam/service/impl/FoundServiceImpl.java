package com.iamk.weTeam.service.impl;

import com.iamk.weTeam.common.config.MiniProgramConfig;
import com.iamk.weTeam.common.utils.DateUtil;
import com.iamk.weTeam.model.entity.Found;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.UserRepository;
import com.iamk.weTeam.service.FoundService;
import com.iamk.weTeam.wxMp.config.WxMpProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@AllArgsConstructor
@Slf4j
@Service
public class FoundServiceImpl implements FoundService {


    @Resource
    UserRepository userRepository;
    @Autowired
    MiniProgramConfig miniProgramConfig;
    @Autowired
    private WxMpProperties wxMpProperties;

    private final WxMpService wxService;

    @Async
    @Override
    public void found(Found found) {

        log.info("新的竞赛/活动发现: " + found);
        WxMpProperties.MpConfig mpConfig = wxMpProperties.getConfigs().get(0);
        // 填充模板
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(wxMpProperties.getMyOpenId())
                .templateId(mpConfig.getInformTemplateId())
                .build();
        /*
         * 模板信息
         */
        String type = found.getType()==0?"竞赛":"活动";
        templateMessage.addData(new WxMpTemplateData("first", "发现新的" + type, "#FF0000"));
        templateMessage.addData(new WxMpTemplateData("keyword1", "url: " + found.getUrl(), "#173177"));

        User user = userRepository.findById(found.getUserId()).orElse(null);
        String name = user ==null? "null" :user.getNickname();
        String userName = user ==null? "null" :user.getUsername();
        String contact = found.getContact();
        if (contact == null || "".equals(contact)) {
            contact = user.getContact();
        }

        templateMessage.addData(new WxMpTemplateData("keyword2", "name: " + name, "#173177"));
        templateMessage.addData(new WxMpTemplateData("keyword3", "contact: " + contact, "#173177"));
        templateMessage.addData(new WxMpTemplateData("keyword4", "userName: " + userName, "#173177"));

        String time = DateUtil.format(found.getCreateTime());
        templateMessage.addData(new WxMpTemplateData("keyword5", time, "#173177"));
        templateMessage.addData(new WxMpTemplateData("remark", "remark: " + found.getRemark(), "#173177"));
        try {
            wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (Exception e) {
            log.error("推送失败：" + e.getMessage());
        }

    }
}
