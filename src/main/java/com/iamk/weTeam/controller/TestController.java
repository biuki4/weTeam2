package com.iamk.weTeam.controller;

import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.UnicomRuntimeException;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.DateUtil;
import com.iamk.weTeam.common.utils.RedisUtil;
import com.iamk.weTeam.common.utils.ResultUtil;

import com.iamk.weTeam.model.entity.Game;
import com.iamk.weTeam.model.entity.GameTag;
import com.iamk.weTeam.repository.GameRepository;
import com.iamk.weTeam.repository.GameTagRepository;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class TestController {

    @Resource
    GameRepository gameRepository;
    @Resource
    GameTagRepository gameTagRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisUtil redisUtil;

    @PassToken
    @GetMapping("/test2")
    public boolean test() {
        System.out.println("开始...");
        //这里故意造成一个异常，并且不进行处理
        Integer.parseInt("abc123");
        return true;
    }
    @PassToken
    @GetMapping("/testNull")
    public boolean testNull() {
        System.out.println("开始...");
        //这里故意造成一个空指针的异常，并且不进行处理
        String str = null;
        str.equals("111");
        return true;
    }
    @PassToken
    @PostMapping("/testBizException")
    public boolean testBizException() {

        System.out.println("开始...");
        //如果姓名为空就手动抛出一个自定义的异常！
        String userName = null;
        if (userName == null) {
            throw new UnicomRuntimeException(UnicomResponseEnums.BAD_REQUEST, "1");
        }
        return true;
    }
    @PassToken
    @GetMapping("/testSuccess")
    public ResultUtil testSuccess() {
        Map<String, String> map = new HashMap<>();
        map.put("A", "a");
        map.put("B", "b");
        map.put("C", "c");
        return ResultUtil.success(map);
    }
    @PassToken
    @RequestMapping("/testError")
    public ResultUtil testError() {
        return ResultUtil.error("099", "错误错误错误");

    }

    /**
     * 测试接收数组参数
     * @return
     */
    @PassToken
    @RequestMapping("/testArray")
    public ResultUtil testArray(@RequestParam String ids) {

        List<GameTag> all = gameTagRepository.findAll();
        System.out.println(all.toString());
        System.out.println(all.size());
        all.remove(0);
        System.out.println(all.toString());
        System.out.println(all.size());
        return ResultUtil.success();
    }

    @PassToken
    @RequestMapping("/testRedis")
    public ResultUtil testRedis(@RequestParam String ids) {
        redisUtil.set("name",ids);
        System.out.println(redisUtil.getExpire("name"));
        return ResultUtil.success(redisUtil.get("name"));
    }

    @PassToken
    @RequestMapping("/uploadImg")
    public String upload(MultipartFile file){
        System.out.println("上传文件");
        System.out.println(file);
        return "成功";
    }

    /**
     * 测试使用yml属性
     * @return
     */
    // @Value("${file.path}")
    // private String test;

    @PassToken
    @RequestMapping("/yml")
    public String testYml(){
        // System.out.println(test);
        return "成功";
    }

    @PassToken
    @RequestMapping("/root")
    public String testRootPath(){
        String root = System.getProperty("user.dir");
        System.out.println(root);
        // String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        // String path = ResourceUtils.getURL("classpath:").getPath();
        // File upload = new File(path.getAbsolutePath(),"static/images/upload/");
        // if(!upload.exists()) upload.mkdirs();
        // System.out.println("upload url:"+upload.getAbsolutePath());
        return "成功";
    }

    @PassToken
    @RequestMapping("/testDate")
    public void t(){
        DateFormat sdfmat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Game game = gameRepository.findById(78).orElse(null);
        Date gameEndTime = game.getGameEndTime();
        Date date = new Date();
        boolean b = DateUtil.compareDate(sdfmat.format(date), sdfmat.format(gameEndTime));
        System.out.println(b);
        System.out.println(sdfmat.format(date));
        System.out.println(sdfmat.format(gameEndTime));

        System.out.println(gameEndTime);
        System.out.println(date);
        System.out.println();

    }

    public static void main(String[] args) throws ParseException {

        DateFormat sdfmat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Date date = new Date();
        // String parse = sdf.format(date);
        // Date parse1 = sdf.parse(parse);
        // System.out.println(date);
        // System.out.println(parse);
        // System.out.println(parse1);
        // System.out.println(date.getTime());
        // System.out.println(parse1.getTime());
        // long time = new Date().getTime();

        // String format = sdfmat.format(date);
        // String format1 = sdf.format(date);
        // System.out.println(format);
        // System.out.println(format1);
        // Date parse = sdfmat.parse(format);
        // Date parse1 = sdf.parse(format1);
        // System.out.println(parse);
        // System.out.println(parse1);
        // String format2 = sdfmat.format(parse1);
        // System.out.println(format2);


        // DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // fmt.parse(date);

        // logger.info("未知异常！原因是:info");
        // logger.error("未知异常！原因是:error");
        // logger.warn("未知异常！原因是:warn");
        // log.info("log4j2 test date: {}  info: {}", new Date(), "请关注公众号：Felordcn");


    }


}
