package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.Activity;
import com.iamk.weTeam.model.vo.ActivityVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    List<Activity> findAll(Specification spec, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update activity a set a.poster_url=:url where a.id=:id", nativeQuery = true)
    void updatePosterUrl(String url, Integer id);

    List<Activity> findAllByIdInAndRegisterEndTimeGreaterThanEqualOrderByPostTimeDesc(List<Integer> ids, Date parse1);

    List<Activity> findAllByIdInAndRegisterEndTimeLessThanOrderByPostTimeDesc(List<Integer> ids, Date parse1);

    @Query(value = "select a.id, a.poster_url from activity a ORDER BY a.post_time DESC limit 0,3", nativeQuery = true)
    List<Map<String, Object>> findSlideshow();

    List<ActivityVo> findTop10ByRegisterEndTimeGreaterThanEqualOrderByPostTimeDesc(Date date);

}
