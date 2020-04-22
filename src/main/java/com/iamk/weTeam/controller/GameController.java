package com.iamk.weTeam.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.*;
import com.iamk.weTeam.model.entity.*;
import com.iamk.weTeam.model.vo.GameTagVo;
import com.iamk.weTeam.repository.*;
import com.iamk.weTeam.model.vo.GameUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/game")
@SuppressWarnings("unchecked")      // 清除屎黄色背景
public class GameController {

    @Resource
    GameRepository gameRepository;
    @Resource
    GameTagRecordRepository gameTagRecordRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    GameTagRepository gameTagRepository;
    @Resource
    GameCategoryRepository gameCategoryRepository;
    @Resource
    GameAttentionRepository gameAttentionRepository;
    @Resource
    AdminRepository adminRepository;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 多条件查询
     * @param key   关键字
     * @param tag   标签
     * @param gameSource    来源
     * @param gameType  级别
     * @param time  时间
     * @param currentPage   当前页
     * @param pageSize  页面大小
     * @return
     */
    @PassToken
    @PostMapping(value= "/games")
    public ResponseEntity games(@RequestParam(defaultValue = "") String key,
                                @RequestParam(defaultValue = "") String tag,
                                @RequestParam(defaultValue = "") String category,
                                @RequestParam(defaultValue = "") String gameSource,
                                @RequestParam(defaultValue = "") String gameType,
                                @RequestParam(defaultValue = "") String time,
                                @RequestParam(defaultValue = "1") Integer currentPage,
                                @RequestParam(defaultValue = "10") Integer pageSize) {

        // System.out.println("currentPage: " + currentPage);
        // System.out.println("category: " + category);
        // System.out.println("gameSource: " + gameSource);
        // System.out.println("gameType: " + gameType );
        // System.out.println("time: " + time);

        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "postTime"));
        // gameName
        Specification spec = SpecificationFactory.containsLike("gameName", key);
        // gameType
        if(StringUtils.isNotBlank(gameType) && !"[]".equals(gameType)){
            Integer[] types = MyUtils.toIntegerArray(gameType);
            spec = spec.and(SpecificationFactory.in("gameType", Arrays.asList(types)));
        }
        // gameSource
        if(StringUtils.isNotBlank(gameSource) && !"[]".equals(gameSource)){
            String[] sources = MyUtils.toStringArray(gameSource);
            spec = spec.and(SpecificationFactory.in("gameSource", sources));
        }
        // category
        if(StringUtils.isNotBlank(category) && !"".equals(category)){
            spec = spec.and(SpecificationFactory.join_equal("gameCategory", "id", Integer.parseInt(category)));
        }

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String parse = sdf.format(date);
            Date parse1 = sdf.parse(parse);
            // time
            switch (time) {
                // 一个月前
                case "1":
                    spec = spec.and(SpecificationFactory.lessThanOrEqualTo("postTime", DateUtil.getBeforeMonth(-1)));
                    break;
                // 三个月前
                case "2":
                    spec = spec.and(SpecificationFactory.lessThanOrEqualTo("postTime", DateUtil.getBeforeMonth(-3)));
                    break;
                // 半年前
                case "3":
                    spec = spec.and(SpecificationFactory.lessThanOrEqualTo("postTime", DateUtil.getBeforeMonth(-6)));
                    break;
                // 一年前
                case "4":
                    spec = spec.and(SpecificationFactory.lessThanOrEqualTo("postTime", DateUtil.getBeforeYear(-1)));
                    break;
                // 正在进行
                case "5":
                    spec = spec.and(SpecificationFactory.greaterThan("gameEndTime", parse1));
                    break;
                // 已结束
                case "6":
                    spec = spec.and(SpecificationFactory.lessThan("gameEndTime", parse1));
                    break;
                default:
                    spec = spec.and(SpecificationFactory.greaterEqualThan("gameEndTime", parse1));
                    // spec = spec.and(SpecificationFactory.greaterEqualThan("gameEndTime",new Date()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        /*
         * 一个待解决的bug，直接用games，game.remove会报错
         * 另外，不会使用specification 对 @ManyToMany的关系进行联表查询
         */
        List<Game> games = gameRepository.findAll(spec, pageable);
        // List<Game> g = new ArrayList<Game>();
        // g.addAll(games);

        // tag
        // if(StringUtils.isNotBlank(tag) && !"[]".equals(tag)) {
        //     Integer[] tags = MyUtils.toIntegerArray(tag);
        //     for (Integer t : tags ) {
        //         GameTag gt = gameTagRepository.findById(t).orElse(null);
        //         int len = g.size();
        //         for (int i = 0; i < len; i++) {
        //             Set<GameTag> gameTags = g.get(i).getGameTags();
        //             if(gameTags.size() == 0){
        //                 g.remove(i);
        //                 len--;
        //                 i--;
        //                 continue;
        //             }
        //             if(gt!=null && !gameTags.contains(gt)){
        //                 g.remove(i);
        //                 len--;
        //                 i--;
        //             }
        //         }
        //     }
        // }
        return ResponseEntity.ok(ResultUtil.success(games));
    }

    // /**
    //  * 根据 name、source、tag 模糊查询
    //  *
    //  * @param key
    //  * @param currentPage
    //  * @param pageSize
    //  * @return
    //  */
    // @PassToken
    // @GetMapping("/gameList")
    // public ResponseEntity findAllGame(@RequestParam(defaultValue = "") String key,
    //                                   @RequestParam(defaultValue = "1") Integer currentPage,
    //                                   @RequestParam(defaultValue = "10") Integer pageSize) {
    //
    //     List<Game> games = new ArrayList<>();
    //     Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "postTime"));
    //     String k = "%" + key + "%";
    //     // source or name
    //     games = gameRepository.findByGameSourceContainingOrGameNameContaining(key, key, pageable);
    //     System.out.println(games);
    //     // tag
    //     List<Integer> ids = new ArrayList<Integer>();
    //     if(StringUtils.isNotBlank(key)){
    //         ids = gameTagRecordRepository.findByTagName(key);
    //     }
    //     if(!ids.isEmpty()){
    //         games.addAll(gameRepository.findByIdIn(ids, pageable));
    //         games.sort(Comparator.comparing(Game::getPostTime));
    //         // 去重
    //         Set<Game> gameSet = new HashSet<>(games);
    //         List<Game> list = new ArrayList<>(gameSet);
    //         list.forEach(System.out::println);
    //         return ResponseEntity.ok(ResultUtil.success(list));
    //     }
    //     return ResponseEntity.ok(ResultUtil.success(games));
    // }

    /**
     * 根据分类查找
     * @param id    categoryId
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PassToken
    @GetMapping("/gameList")
    public ResponseEntity findByCategory(@RequestParam(defaultValue = "-1") Integer id,
                                         @RequestParam(defaultValue = "1") Integer currentPage,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        // System.out.println("id: " + id);
        // System.out.println("currentPage: " + currentPage);
        // System.out.println("pageSize: " + pageSize);
        List<Game> games = null;
        try {
            Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "postTime"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String parse = sdf.format(date);
            Date parse1 = sdf.parse(parse);
            // 全部
            if(id==-1){
                games = gameRepository.findByGameEndTimeGreaterThanEqual(parse1, pageable);
            }else if(id==0) {
                // 往期精彩
                games = gameRepository.findByGameEndTimeLessThan(parse1, pageable);
            }else{
                GameCategory gameCategory = gameCategoryRepository.findById(id).orElse(null);
                games = gameRepository.findByGameCategoryAndGameEndTimeGreaterThanEqual(gameCategory,parse1,pageable);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.ok(ResultUtil.success(games));
    }

    /**
     * 轮播图数据
     * @return
     */
    @PassToken
    @GetMapping("/getSlideshow")
    public ResultUtil getSlideshow(){
        // redis中取
        String key = "slideshow";
        if(redisUtil.hasKey(key)){
            Object o = redisUtil.get(key);
            return ResultUtil.success(o);
        }
        System.out.println("sql...");
        List<Map<String, Object>> games = gameRepository.findSlideshow();
        // 存到redis 1h
        redisUtil.set(key, games, 60*60);
        return ResultUtil.success(games);
    }

    /**
     * 根据id查找比赛
     * @param id
     * @return
     */
    @PassToken
    @RequestMapping("/{id}")
    public ResultUtil findById(@PathVariable int id, HttpServletRequest httpServletRequest) {
        // redis中取
        // String key = RedisKeyUtil.createKey("game", "gameId", id);
        // if(redisUtil.hasKey(key)){
        //     System.out.println("redis...");
        //     Object o = redisUtil.get(key);
        //     System.out.println(o.);
        //     System.out.println(o);
        //     return ResultUtil.success(o);
        // }
        // System.out.println("sql...");
        // sql中
        JSONObject jsonObject = new JSONObject();
        try {
            // game
            Game game = gameRepository.findById(id).orElse(null);
            if(game==null) {
                return ResultUtil.error(UnicomResponseEnums.NO_GAME);
            }
            // poster
            User poster = userRepository.findById(game.getPostId()).orElse(null);
            // isCollect
            String token = httpServletRequest.getHeader("token");
            jsonObject.put("isCollect", false);
            if(token!=null && !"".equals(token)){
                Integer userId = MyUtils.getUserIdFromToken(token);
                GameAttention ga = gameAttentionRepository.findByUserIdAndGameId(userId, id);
                if(ga != null){
                    jsonObject.put("isCollect", true);
                }
            }
            jsonObject.put("game", game);
            jsonObject.put("poster", poster);
            System.out.println(game);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        // System.out.println(game);
        // 存到redis 1H
        // redisUtil.set(key, jsonObject, 60*60);
        return ResultUtil.success(jsonObject);
    }

    /**
     * 根据tag查找所有比赛
     * @param tag
     * @param page
     * @param size
     * @return
     */
    @PassToken
    @GetMapping("/tag")
    public ResponseEntity findByTag(@RequestParam(defaultValue = "") String tag,
                                    @RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        List<Game> games = new ArrayList<>();
        try{
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postTime"));
            List<Integer> ids = gameTagRecordRepository.findByTagName(tag);
            games = gameRepository.findByIdIn(ids, pageable);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return ResponseEntity.ok(BasicResponse.ok().data(games));
    }

    /**
     * 查找用户参与的比赛
     * @param id    用户id
     * @return
     */
    // @GetMapping("/join/{id}")
    // public ResultUtil findGames(@PathVariable Integer id){
    //     // 查询比赛及队友
    //     List<GameJoin> gameJoins = gameJoinRepository.findByUserId(id);
    //     //
    //     List<GameUser> games = new ArrayList<>();
    //     //
    //     for(GameJoin gameJoin: gameJoins){
    //         int gameId = gameJoin.getGameId();
    //         int teamId = gameJoin.getGameTeamId();
    //         // game tags
    //         Game game = gameRepository.findById(gameId).orElse(null);
    //
    //         //team
    //         List<Map<String, String>> team = userRepository.findTeamMember(teamId, gameId);
    //         GameUser gameUser = new GameUser(game, team);
    //         games.add(gameUser);
    //     }
    //     return ResultUtil.success(games);
    // }



    /**
     * 查询收藏的比赛
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/attentionList")
    public ResultUtil findGameAttention(@RequestParam(defaultValue = "0") Integer id, HttpServletRequest httpServletRequest){
        JSONObject jsonObject = new JSONObject();
        try{
            if(id==0){
                String token = httpServletRequest.getHeader("token");
                id = MyUtils.getUserIdFromToken(token);
            }
            List<Integer> ids = gameAttentionRepository.findGameIdById(id);
            // 正在进行
            List<Game> games = gameRepository.findAllByIdInAndGameEndTimeGreaterThanEqual(ids, new Date());
            // 已经结束
            List<Game> games2 = gameRepository.findAllByIdInAndGameEndTimeLessThan(ids, new Date());
            jsonObject.put("curGame", games);
            jsonObject.put("endGame", games2);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(jsonObject);
    }


    /**
     * 收藏比赛
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/collect/{id}")
    public ResultUtil collect(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        try{
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            GameAttention ga = new GameAttention();
            ga.setGameId(id);
            ga.setUserId(userId);
            gameAttentionRepository.save(ga);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 取消收藏
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/unCollect/{id}")
    public ResultUtil unCollect(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        try{
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            GameAttention ga = new GameAttention();
            ga.setGameId(id);
            ga.setUserId(userId);
            gameAttentionRepository.delete(ga);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }

    /**
     * 发布竞赛
     * @param gtVo
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/add")
    public ResultUtil addGame(@RequestBody GameTagVo gtVo, HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("token");
        Integer postId = MyUtils.getUserIdFromToken(token);
        // 非管理员管理员
        Admin admin = adminRepository.findByUserId(postId);
        if(admin==null){
            return ResultUtil.error(UnicomResponseEnums.NOT_ADMIN);
        }
        // poster
        Game game = gtVo.getGame();
        try{
            game.setPostId(postId);
            game.setPostTime(new Date());
            game.setGameViews(0);
            game = gameRepository.save(game);
            // tags
            String[] tagList = MyUtils.toStringArray(gtVo.getTags());
            for(String tag:tagList){
                // gameTag
                GameTag insertTag = new GameTag();
                insertTag.setTagName(tag);
                // gameTagRecord
                GameTagRecord gtr = new GameTagRecord();
                gtr.setGameId(game.getId());
                // 判断是否存在该标签
                GameTag currTag = gameTagRepository.findByTagName(tag);
                if(currTag == null){
                    GameTag savedTag = gameTagRepository.save(insertTag);
                    gtr.setTagId(savedTag.getId());
                    gameTagRecordRepository.save(gtr);
                }else {
                    gtr.setTagId(currTag.getId());
                    gameTagRecordRepository.save(gtr);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(game);
    }

    /**
     * 编辑竞赛
     * @param gtVo
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/update")
    public ResultUtil updateGame(@RequestBody GameTagVo gtVo, HttpServletRequest httpServletRequest){
        Game g = gtVo.getGame();
        try{
            Game old_g = gameRepository.findById(g.getId()).orElse(null);
            // game is null
            if(old_g==null) {
                return ResultUtil.error(UnicomResponseEnums.NO_GAME);
            }

            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            // 非管理员
            Admin admin = adminRepository.findByUserId(userId);
            if(admin==null){
                return ResultUtil.error(UnicomResponseEnums.NOT_ADMIN);
            }
            // 权限不足
            if(admin.getUserType() > 0 && g.getPostId() != userId) {
                return ResultUtil.error(UnicomResponseEnums.NO_RIGHT);
            }
            // 更新game
            UpdateTool.copyNullProperties(old_g, g);
            System.out.println(old_g);
            System.out.println(g);
            gameRepository.save(g);
            // tags  上方save后tags已经删除
            String[] tagList = MyUtils.toStringArray(gtVo.getTags());
            System.out.println(tagList.toString());
            for(String tag:tagList){
                System.out.println(tag);
                // gameTag
                GameTag insertTag = new GameTag();
                insertTag.setTagName(tag);
                // gameTagRecord
                GameTagRecord gtr = new GameTagRecord();
                gtr.setGameId(g.getId());
                // 判断是否存在该标签
                GameTag currTag = gameTagRepository.findByTagName(tag);
                if(currTag == null){
                    GameTag savedTag = gameTagRepository.save(insertTag);
                    gtr.setTagId(savedTag.getId());
                    gameTagRecordRepository.save(gtr);
                }else {
                    gtr.setTagId(currTag.getId());
                    gameTagRecordRepository.save(gtr);
                }
            }
            // 删除redis缓存
            // String key = RedisKeyUtil.createKey("game", "gameId", g.getId());
            // if(redisUtil.hasKey(key)){
            //     redisUtil.del(key);
            // }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success(g);
    }

    /**
     * 删除竞赛
     * @param id 竞赛id
     * @param httpServletRequest
     * @return
     */
    @PassToken
    @RequestMapping("/delete/{id}")
    public ResultUtil delete(@PathVariable Integer id, HttpServletRequest httpServletRequest){
        try{
            Game g = gameRepository.findById(id).orElse(null);
            // game is null
            if(g==null) {
                return ResultUtil.error(UnicomResponseEnums.NO_GAME);
            }
            String token = httpServletRequest.getHeader("token");
            Integer userId = MyUtils.getUserIdFromToken(token);
            // 非管理员
            Admin admin = adminRepository.findByUserId(userId);
            if(admin==null){
                return ResultUtil.error(UnicomResponseEnums.NOT_ADMIN);
            }
            // 权限不足
            if(admin.getUserType() > 0 && !g.getPostId().equals(userId)) {
                return ResultUtil.error(UnicomResponseEnums.NO_RIGHT);
            }
            gameRepository.deleteById(id);
            // 删除redis缓存
            // String key = RedisKeyUtil.createKey("game", "gameId", id);
            // if(redisUtil.hasKey(key)){
            //     redisUtil.del(key);
            //     System.out.println("delete: " + key);
            // }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResultUtil.success();
    }



    /**
     * 我的发布
     * @param httpServletRequest
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestMapping("/myPost")
    public ResultUtil myPost(@RequestParam(defaultValue = "0") Integer id, HttpServletRequest httpServletRequest,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        JSONObject jsonObject = new JSONObject();
        try {
            if(id==0){
                String token = httpServletRequest.getHeader("token");
                id = MyUtils.getUserIdFromToken(token);
            }
            Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "postTime"));
            // 正在进行
            List<Game> curGame = gameRepository.findByPostIdAndGameEndTimeGreaterThanEqual(id, new Date(), pageable);
            // 已经结束
            List<Game> endGame = gameRepository.findByPostIdAndGameEndTimeLessThan(id, new Date(), pageable);

            jsonObject.put("curGame", curGame);
            jsonObject.put("endGame", endGame);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return ResultUtil.success(jsonObject);
    }


}



