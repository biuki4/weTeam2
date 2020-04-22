package com.iamk.weTeam.repository;


import com.iamk.weTeam.model.entity.GameTagRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameTagRecordRepository extends JpaRepository<GameTagRecord, Integer> {
    @Query(value = "select game_id from game_tag g1, game_tag_record g2 where g1.id=g2.tag_id and g1.tag_name=?1", nativeQuery = true)
    List<Integer> findByTagName(String tag);

    void deleteByGameId(Integer id);
}
