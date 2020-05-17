package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.Game;
import com.iamk.weTeam.model.entity.GameCategory;
import com.iamk.weTeam.model.vo.GameVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GameRepository extends JpaRepository<Game, Integer> {

    List<Game> findByIdIn(List<Integer> ids, Pageable pageable);

    @Query(value = "select g.game_name, GROUP_CONCAT(gt.id) as ids from game g, game_tag gt, game_tag_record gtr" +
            " where g.id = gtr.game_id and gt.id = gtr.tag_id and g.id = 1 GROUP BY g.game_name", nativeQuery = true)
    List<Map<String, String>> findByIdWithTags(Integer id);

    List<Game> findAll(Specification<Game> spec, Pageable pageable);

    List<Game> findByRegisterEndTimeLessThan(Date date, Pageable pageable);

    List<Game> findByRegisterEndTimeGreaterThanEqual(Date date, Pageable pageable);

    List<Game> findByGameCategoryAndRegisterEndTimeGreaterThanEqual(GameCategory gameCategory, Date date, Pageable pageable);

    @Query(value = "select g.id, g.poster_url from game g ORDER BY g.post_time DESC limit 0,3", nativeQuery = true)
    List<Map<String, Object>> findSlideshow();

    @Transactional
    @Modifying
    @Query(value = "update game g set g.poster_url=:url where g.id=:id", nativeQuery = true)
    void updatePosterUrl(String url, Integer id);

    @Query(value = "select g.game_name from game g where g.id=:i", nativeQuery = true)
    String findGameNameById(int i);

    List<Game> findByPostIdAndRegisterEndTimeGreaterThanEqualOrderByPostTimeDesc(Integer id, Date date, Pageable pageable);

    List<Game> findByPostIdAndRegisterEndTimeLessThanOrderByPostTimeDesc(Integer id, Date date, Pageable pageable);

    List<Game> findAllByIdInAndRegisterEndTimeGreaterThanEqualOrderByPostTimeDesc(List<Integer> ids, Date date);

    List<Game> findAllByIdInAndRegisterEndTimeLessThanOrderByPostTimeDesc(List<Integer> ids, Date date);

    List<GameVo> findTop10ByRegisterEndTimeGreaterThanEqualOrderByPostTimeDesc(Date date);
}
