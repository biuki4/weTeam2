package com.iamk.weTeam.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.constant.MiniProgramConstant;
import com.iamk.weTeam.common.constant.UnionConstant;
import com.iamk.weTeam.common.utils.DateUtil;
import com.iamk.weTeam.common.utils.HttpClientUtil;
import com.iamk.weTeam.common.utils.MD5Util;
import com.iamk.weTeam.model.dto.ApplyDTO;
import com.iamk.weTeam.model.entity.TeamUser;
import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.repository.GameRepository;
import com.iamk.weTeam.repository.TeamUserRepository;
import com.iamk.weTeam.repository.UserRepository;
import com.iamk.weTeam.service.TeamService;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.iamk.weTeam.common.utils.MyUtils.sha1;

@AllArgsConstructor
@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    @Resource
    GameRepository gameRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    TeamUserRepository teamUserRepository;
    @Autowired
    private WxMpProperties wxMpProperties;

    private final WxMpService wxService;

    /**
     * 去除所有单双引号
     * @param str
     * @return
     */
    private String format(String str) {
        str = str.replaceAll("\"", " ");
        str = str.replaceAll("\'", " ");
        str = str.replaceAll("\\s+", " ");
        return str;
    }

    /**
     * 推送到i瓜大
     * @param applyDTO
     */
    @Async
    @Override
    public void infoNewApplyToUnion(ApplyDTO applyDTO) {
        log.info("新的组队申请: " + applyDTO);

        User leader = userRepository.findById(applyDTO.getLeaderId()).orElse(null);
        // 拒绝推送
        if(leader==null || leader.getShowMe()==1) {
            // 设置错误原因
            TeamUser t = teamUserRepository.findByTeamIdAndUserId(applyDTO.getTeamId(), applyDTO.getApplyId());
            t.setSendLog("拒绝推送~");
            teamUserRepository.save(t);
            return;
        }
        // 未关注公众号
        if(leader.getUnionId()==null || "".equals(leader.getUnionId())) {
            TeamUser t = teamUserRepository.findByTeamIdAndUserId(applyDTO.getTeamId(), applyDTO.getApplyId());
            t.setSendLog("没有unionId,未关注公众号~");
            teamUserRepository.save(t);
            return;
        }
        // System.out.println("-------------------");
        Map<String, String> params = new HashMap<>();
        params.put("weappid", MiniProgramConstant.APPID);

        // sign
        String str1 = MD5Util.md5(UnionConstant.WEAPPSECRET + MiniProgramConstant.APPID + UnionConstant.WEAPPSECRET);
        String sign = sha1(str1 + leader.getUnionId());
        params.put("sign", sign);

        // templateId
        params.put("template_id", UnionConstant.TemplateId);

        // unionId
        params.put("weapp_unionid", leader.getUnionId());

        // miniProgram_id
        params.put("miniprogram_id", MiniProgramConstant.APPID);

        // miniProgram_path
        // params.put("miniprogram_path", "pages/team/my-team");

        // content
        JSONObject content = new JSONObject();
        content.put("first", new String[]{"您的队伍有新的申请！", "#FF0000"});

        // 联系方式
        String contact = userRepository.findContactById(applyDTO.getApplyId());
        if(contact == null || "".equals(contact)) {
            contact = "暂未设置";
        }
        String gName = gameRepository.findGameNameById(applyDTO.getGameId());

        content.put("keyword1", new String[]{gName, "#173177"});
        content.put("keyword2", new String[]{applyDTO.getTName(), "#173177"});
        content.put("keyword4", new String[]{contact, "#173177"});
        content.put("keyword5", new String[]{applyDTO.getReason(), "#173177"});
        content.put("remark", new String[]{"点击进入小程序处理~", "#173177"});

        // 处理单双引号
        for (Map.Entry<String, Object> entry : content.entrySet()) {
            String[] value = (String[]) entry.getValue();
            value[0] = format(value[0]);
            content.put(entry.getKey(), value);
        }
        // System.out.println(content.toJSONString());
        params.put("content", content.toJSONString());

        // post发送
        JSONObject jsonObject = JSON.parseObject(HttpClientUtil.doPost(UnionConstant.SEND_TEMPLATE_URL, params, "utf-8"));
        // System.out.println(jsonObject);

        // 更新sendLog
        TeamUser t = teamUserRepository.findByTeamIdAndUserId(applyDTO.getTeamId(), applyDTO.getApplyId());
        t.setSendLog((String) jsonObject.get("msg"));
        t.setSendStatus(0);
        Object success = jsonObject.get("success");
        if(success!=null && (boolean)success) {
            t.setSendLog("发送成功");
            t.setSendStatus(1);
        }
        Object statusCode = jsonObject.get("status_code");
        if(statusCode!=null) {
            t.setSendLog((String) jsonObject.get("message"));
        }
        teamUserRepository.save(t);
    }

    /**
     * 推送到NupTeam公众号
     * @param applyDTO
     */
    @Async
    @Override
    public void infoNewApply(ApplyDTO applyDTO) {
        log.info("新的组队申请: " + applyDTO);
        User leader = userRepository.findById(applyDTO.getLeaderId()).orElse(null);
        if(leader==null || leader.getShowMe()==1) {
            log.info("设置拒绝推送~");
            return;
        }
        WxMpProperties.MpConfig mpConfig = wxMpProperties.getConfigs().get(0);
        // miniProgram 跳转小程序
        WxMpTemplateMessage.MiniProgram miniProgram = new WxMpTemplateMessage.MiniProgram();
        miniProgram.setAppid(MiniProgramConstant.APPID);
        miniProgram.setPagePath("/pages/team/my-team?id=" + applyDTO.getApplyId());
        miniProgram.setUsePath(false);
        // 填充模板
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser("o0fNJxCNgwtbQ4sr3GcKEEZtFY_M")     //要推送的用户openid
                .templateId(mpConfig.getApplyTemplateId())       //模版id
                .miniProgram(miniProgram)
                .build();
        /*
         * 模板信息
         */
        templateMessage.addData(new WxMpTemplateData("first", "您的队伍有新的成员申请加入！", "#FF0000"));
        // 竞赛名
        String gName = gameRepository.findGameNameById(applyDTO.getGameId());
        templateMessage.addData(new WxMpTemplateData("keyword1", gName, "#173177"));
        // 队伍名
        templateMessage.addData(new WxMpTemplateData("keyword2", applyDTO.getTName(), "#173177"));
        // 联系方式
        String contact = userRepository.findContactById(applyDTO.getApplyId());
        if(contact == null || "".equals(contact)) {
            contact = "暂未设置";
        }
        templateMessage.addData(new WxMpTemplateData("keyword3", contact, "#173177"));
        templateMessage.addData(new WxMpTemplateData("keyword4", applyDTO.getReason(), "#173177"));
        // 备注
        templateMessage.addData(new WxMpTemplateData("remark", "点击进入小程序处理！", "#173177"));
        try {
            wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (Exception e) {
            log.error("推送失败：" + e.getMessage());
        }
    }

}
