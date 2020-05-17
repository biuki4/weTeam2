package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.config.MiniProgramConfig;
import com.iamk.weTeam.model.dto.MessageDTO;
import com.iamk.weTeam.repository.GameRepository;
import com.iamk.weTeam.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 公众号
 */
@Slf4j
@RestController
@RequestMapping("/api/wx")
public class WeChatController {

    private static final String TOKEN = "iamk";

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    MiniProgramConfig miniProgramConfig;
    @Resource
    GameRepository gameRepository;
    @Autowired
    TeamService teamService;


    public String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
        return sb.toString();
    }

    public String sha1(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            // 创建 16进制字符串
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 验证
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     * @throws NoSuchAlgorithmException
     */
    @PassToken
    @RequestMapping("/")
    public String test(@RequestParam String signature,
                       @RequestParam String timestamp,
                       @RequestParam String nonce,
                       @RequestParam String echostr) throws NoSuchAlgorithmException {
        System.out.println(signature);
        System.out.println(timestamp);
        System.out.println(nonce);
        System.out.println(echostr);

        // 1.排序
        String sortString = sort(TOKEN, timestamp, nonce);
        // 2.sha1加密
        String myString = sha1(sortString);
        // 3.字符串校验
        if (myString != null && myString != "" && myString.equals(signature)) {
            log.info("微信-签名校验通过");
            //如果检验成功原样返回echostr，微信服务器接收到此输出，才会确认检验完成。
            log.info("回复给微信的 echostr 字符串:{}", echostr);
            return echostr;
        } else {
            log.error("微信-签名校验失败");
            return "";
        }

    }

    @PassToken
    @RequestMapping("/sendMessage")
    public Object sendMessage() {
        MessageDTO messageDTO = new MessageDTO();
        // 模板id
        messageDTO.setTemplateId("QNYtqhcYKHh3jUtx3233CkutLRsYwjFgg1chw34ucfE");
        // 用户openId
        messageDTO.setToUser("o0fNJxCNgwtbQ4sr3GcKEEZtFY_M");
        // 参数
        messageDTO.getData().put("first", MessageDTO.initData("队伍有新的申请"));
        messageDTO.getData().put("keyword1", MessageDTO.initData("xxx竞赛", "#0000EE"));
        messageDTO.getData().put("keyword2", MessageDTO.initData("xxx队伍", "#00CD00"));
        messageDTO.getData().put("keyword3", MessageDTO.initData("xxx", "#00CD00"));
        messageDTO.getData().put("remark", MessageDTO.initData("快去查看吧！", "#00CD00"));
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
        String assess_Token = "32_a4gFgxeXkXS4GclfN_9BhDtMANH4I_hIfsU03bruXhNum9HjNxCVjYqu9inLcxYCmtlLzTssWBKS6veR4mwtFY3RJpOvsZQVG4tqq8-q52AxsBF7Nq9Ol5uUm2FYy6408cXFyt8oS_wLCL_FQKRiADAICJ";
        String openId = "o0fNJxCNgwtbQ4sr3GcKEEZtFY_M";
        System.out.println(messageDTO);
        ResultUtil resultUtil = restTemplate.postForObject(String.format(url, assess_Token), messageDTO, ResultUtil.class);
        System.out.println(resultUtil);
        return resultUtil;
    }

    // @Async
    // @PassToken
    // @RequestMapping("/sendMessage2")
    // public ResultUtil newApply() throws InterruptedException {
    //     TimeUnit.SECONDS.sleep(3);
    //     System.out.println("发通知啦-----------");
    //     GZHMessageSendVO g = new GZHMessageSendVO();
    //     // 用户openId
    //     g.setTouser("o0fNJxCNgwtbQ4sr3GcKEEZtFY_M");
    //     // 模板id
    //     g.setTemplateId("lJQFG72EBHH2LbPjgfVP1ETCfEoVRROGGgm5ZhOrqi8");
    //     // 小程序appId
    //     g.setAppId(weChatConfig.APPID);
    //     // 小程序跳转
    //     g.setAppId(weChatConfig.APPID);
    //     g.setMiniProgram("/pages/user/team/my-team?id=75");
    //     // 模板参数
    //     Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
    //     data.put("first", GZHMessageSendVO.initData("您的队伍有新的成员申请加入！", "#FF0000"));
    //     data.put("keyword1", GZHMessageSendVO.initData("keyword1", "#173177"));
    //     data.put("keyword2", GZHMessageSendVO.initData("keyword2", "#173177"));
    //     data.put("keyword3", GZHMessageSendVO.initData("keyword3", "#173177"));
    //     data.put("remark", GZHMessageSendVO.initData("remark", "#173177"));
    //     g.setData(data);
    //     baseApiWeiXinService.SendTemplateMsg(g);
    //     return ResultUtil.success();
    // }

    @PassToken
    @RequestMapping("/sendMessage3")
    public String newApply1() throws InterruptedException {
        System.out.println("----1---");
        // newApply();
        // baseApiWeiXinService.test();
        // teamService.newApply();
        System.out.println("------4-----");
        return "redirect:/sendMessage2";

    }

    public void test() {

    }

}
