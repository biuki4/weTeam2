package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.ActivityAttention;
import com.iamk.weTeam.model.entity.GameAttention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityAttentionRepository extends JpaRepository<ActivityAttention, Integer> {
    @Query(value = "select activity_id from activity_attention where user_id = :id ", nativeQuery = true)
    List<Integer> findActivityIdByUserId(Integer id);

    ActivityAttention findByUserIdAndActivityId(Integer userId, int id);
}
