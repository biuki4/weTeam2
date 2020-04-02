package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.utils.BasicResponse;
import com.iamk.weTeam.common.utils.JwtUtils;
import com.iamk.weTeam.common.utils.RedisUtil;
import com.iamk.weTeam.common.utils.ResultUtil;
import com.iamk.weTeam.model.entity.Message;
import com.iamk.weTeam.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/msg")
public class MessageController {

    @Resource
    MessageRepository messageRepository;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 根据receiverId查询所有消息
     * * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/msgList")
    public ResultUtil findMessages(@RequestParam(defaultValue = "1") Integer currentPage,
                                       @RequestParam(defaultValue = "10") Integer pageSize, HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("token");
        Map<String, String> tokenInfo = JwtUtils.getTokenInfo(token);
        String userId = tokenInfo.get("userId");
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC,"createTime"));
        List<Message> messages = messageRepository.findByReceiverId(Integer.parseInt(userId), pageable);
        return ResultUtil.success(messages);
    }

    /**
     * 根据message id查找消息
     * @param id
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity findMessage(@PathVariable Integer id,
                                       @RequestParam(defaultValue = "0") Integer currentPage,
                                       @RequestParam(defaultValue = "10") Integer pageSize){
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC,"createTime"));
        Page<Message> messages = messageRepository.findById(id, pageable);

        return ResponseEntity.ok(BasicResponse.ok().data(messages));
    }
}
