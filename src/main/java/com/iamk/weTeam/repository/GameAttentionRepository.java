package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.GameAttention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameAttentionRepository extends JpaRepository<GameAttention, Integer> {
    @Query(value = "select game_id from game_attention where user_id = :id ", nativeQuery = true)
    List<Integer> findGameIdById(Integer id);

   GameAttention findByUserIdAndGameId(Integer userId, int id);
}
