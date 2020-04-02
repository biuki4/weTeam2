package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.Game;
import com.iamk.weTeam.model.entity.GameCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GameRepository extends JpaRepository<Game, Integer> {

    Page<Game> findByGameSource(String source, Pageable pageable);

    List<Game> findByGameSourceLike(String source, Pageable pageable);


    Page<Game> findByGameNameLike(String param, Pageable pageable);

    List<Game> findByGameSourceContaining(String source, Pageable pageable);

    List<Game> findByGameSourceLikeOrGameNameLike(String source, String name, Pageable pageable);

    List<Game> findByIdIn(List<Integer> ids, Pageable pageable);

    @Query(value = "select g.game_name, GROUP_CONCAT(gt.id) as ids from game g, game_tag gt, game_tag_record gtr" +
            " where g.id = gtr.game_id and gt.id = gtr.tag_id and g.id = 1 GROUP BY g.game_name", nativeQuery = true)
    List<Map<String, String>> findByIdWithTags(Integer id);

    List<Game> findAll(Specification<Game> spec, Pageable pageable);


    Page<Game> findByGameEndTimeLessThan(Date date, Pageable pageable);

    Page<Game> findByGameCategory(GameCategory id, Pageable pageable);

    List<Game> findByGameCategoryAndGameSourceLikeOrGameNameLike(GameCategory gameCategory, String k, String k1, Pageable pageable);

    List<Game> findByAndGameSourceLikeOrGameNameLikeAndGameEndTimeLessThan(String k, String k1, Date date, Pageable pageable);

    List<Game> findByGameSourceContainingOrGameNameContaining(String key, String key1, Pageable pageable);

    Page<Game> findByGameNameContaining(String key, Pageable pageable);

    List<Game> findByPostId(Integer userId, Pageable pageable);
}
