package com.iamk.weTeam.controller;


import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.common.UnicomResponseEnums;
import com.iamk.weTeam.common.annotation.PassToken;
import com.iamk.weTeam.common.utils.*;
import com.iamk.weTeam.model.entity.*;
import com.iamk.weTeam.model.vo.GameTagVo;
import com.iamk.weTeam.repository.*;
import com.iamk.weTeam.model.vo.GameUser;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/game")
@SuppressWarnings("unchecked")      // 清除屎黄色背景
public class GameController {

    @Resource
    GameRepository gameRepository;
    @Resource
    GameTagRecordRepository gameTagRecordRepository;
    @Resource
    GameJoinRepository gameJoinRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    GameTagRepository gameTagRepository;
    @Resource
    GameCategoryRepository gameCategoryRepository;
    @Resource
    GameAttentionRepository gameAttentionRepository;

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
                                @RequestParam(defaultValue = "") String gameSource,
                                @RequestParam(defaultValue = "") String gameType,
                                @RequestParam(defaultValue = "") String time,
                                @RequestParam(defaultValue = "1") Integer currentPage,
                                @RequestParam(defaultValue = "10") Integer pageSize) {

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
        // time
        if(StringUtils.isNotBlank(time)){
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
            }
        }

        /*
         * 一个待解决的bug，直接用games，game.remove会报错
         * 另外，不会使用specification 对 @ManyToMany的关系进行联表查询
         */
        List<Game> games = gameRepository.findAll(spec, pageable);
        List<Game> g = new ArrayList<Game>();
        g.addAll(games);

        // tag
        if(StringUtils.isNotBlank(tag) && !"[]".equals(tag)) {
            Integer[] tags = MyUtils.toIntegerArray(tag);
            for (Integer t : tags ) {
                GameTag gt = gameTagRepository.findById(t).orElse(null);
                int len = g.size();
                for (int i = 0; i < len; i++) {
                    Set<GameTag> gameTags = g.get(i).getGameTags();
                    if(gameTags.size() == 0){
                        g.remove(i);
                        len--;
                        i--;
                        continue;
                    }
                    if(gt!=null && !gameTags.contains(gt)){
                        g.remove(i);
                        len--;
                        i--;
                    }
                }
            }
        }
        return ResponseEntity.ok(ResultUtil.success(g));
    }

    /**
     * 根据 name、source、tag 模糊查询
     *
     * @param key
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PassToken
    @GetMapping("/gameList")
    public ResponseEntity findAllGame(@RequestParam(defaultValue = "") String key,
                                      @RequestParam(defaultValue = "1") Integer currentPage,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {


        System.out.println(key);
        List<Game> games = new ArrayList<>();
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "postTime"));
        String k = "%" + key + "%";
        // source or name
        games = gameRepository.findByGameSourceContainingOrGameNameContaining(key, key, pageable);
        // tag
        List<Integer> ids = new ArrayList<Integer>();
        if(StringUtils.isNotBlank(key)){
            ids = gameTagRecordRepository.findByTagName(key);
        }
        if(!ids.isEmpty()){
            games.addAll(gameRepository.findByIdIn(ids, pageable));
            games.sort(Comparator.comparing(Game::getPostTime));
            // 去重
            Set<Game> gameSet = new HashSet<>(games);
            List<Game> list = new ArrayList<>(gameSet);
            list.forEach(System.out::println);
            return ResponseEntity.ok(ResultUtil.success(list));
        }
        return ResponseEntity.ok(ResultUtil.success(games));
    }

    /**
     * 根据id查找比赛
     * @param id
     * @return
     */
    @PassToken
    @RequestMapping("/{id}")
    public ResultUtil findById(@PathVariable int id, HttpServletRequest httpServletRequest) {
        JSONObject jsonObject = new JSONObject();
        // game
        Game game = gameRepository.findById(id).orElse(null);
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postTime"));
        List<Game> games = new ArrayList<>();
        List<Integer> ids = gameTagRecordRepository.findByTagName(tag);
        games = gameRepository.findByIdIn(ids, pageable);

        return ResponseEntity.ok(BasicResponse.ok().data(games));
    }

    /**
     * 查找用户参与的比赛
     * @param id    用户id
     * @return
     */
    @GetMapping("/join/{id}")
    public ResultUtil findGames(@PathVariable Integer id){
        // 查询比赛及队友
        List<GameJoin> gameJoins = gameJoinRepository.findByUserId(id);
        //
        List<GameUser> games = new ArrayList<>();
        //
        for(GameJoin gameJoin: gameJoins){
            int gameId = gameJoin.getGameId();
            int teamId = gameJoin.getGameTeamId();
            // game tags
            Game game = gameRepository.findById(gameId).orElse(null);

            //team
            List<Map<String, String>> team = userRepository.findTeamMember(teamId, gameId);
            GameUser gameUser = new GameUser(game, team);
            games.add(gameUser);
        }
        return ResultUtil.success(games);
    }


    /**
     * 根据分类搜索
     * @param id
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PassToken
    @GetMapping("/games/{id}")
    public ResponseEntity findByCategory(@PathVariable int id,
                                         @RequestParam(defaultValue = "1") Integer currentPage,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        System.out.println(currentPage);
        System.out.println(pageSize);
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "postTime"));
        Page<Game> games = null;
        // 全部
        if(id==1){
            games = gameRepository.findAll(pageable);
        }else if(id==2) {
            games = gameRepository.findByGameEndTimeLessThan(new Date(), pageable);
        }else{
            GameCategory gameCategory = gameCategoryRepository.findById(id).orElse(null);
            games = gameRepository.findByGameCategory(gameCategory,pageable);
        }
        for (Game g:games
             ) {
            System.out.println(g.getGameName());
        }
        return ResponseEntity.ok(ResultUtil.success(games));
    }


    /**
     * 查询收藏的比赛
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/attentionList")
    public ResultUtil findGameAttention(@RequestParam(defaultValue = "0") Integer id, HttpServletRequest httpServletRequest){
        System.out.println("123");
        if(id==0){
            String token = httpServletRequest.getHeader("token");
            id = MyUtils.getUserIdFromToken(token);
        }
        List<Integer> ids = gameAttentionRepository.findGameIdById(id);
        List<Game> games = gameRepository.findAllById(ids);
        System.out.println(games);
        return ResultUtil.success(games);
    }


    /**
     * 收藏比赛
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/collect/{id}")
    public ResultUtil collect(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        GameAttention ga = new GameAttention();
        ga.setGameId(id);
        ga.setUserId(userId);
        gameAttentionRepository.save(ga);
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
        String token = httpServletRequest.getHeader("token");
        Integer userId = MyUtils.getUserIdFromToken(token);
        GameAttention ga = new GameAttention();
        ga.setGameId(id);
        ga.setUserId(userId);
        gameAttentionRepository.delete(ga);
        return ResultUtil.success();
    }

    /**
     * 发布竞赛
     * @param gtVo
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/add")
    public ResultUtil updateUser(@RequestBody GameTagVo gtVo, HttpServletRequest httpServletRequest){
        System.out.println(gtVo);
        String token = httpServletRequest.getHeader("token");
        Integer postId = MyUtils.getUserIdFromToken(token);
        // poster
        Game game = gtVo.getGame();
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
        return ResultUtil.success(game);
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
        if(id==0){
            String token = httpServletRequest.getHeader("token");
            id = MyUtils.getUserIdFromToken(token);
        }
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, "postTime"));
        List<Game> byPostId = gameRepository.findByPostId(id, pageable);
        return ResultUtil.success(byPostId);
    }

}



