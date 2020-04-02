package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.UserAttention;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserAttentionRepository extends JpaRepository<UserAttention, Integer> {

    // 关注
    int countByUserId(Integer userId);

    @Query(value = "select attention_id from user_attention where user_id = :id ", nativeQuery = true)
    List<Integer> findByUserId(@Param("id") Integer id);


    // 粉丝
    int countByAttentionId(Integer userId);

    @Query(value = "select user_id from user_attention where attention_id = :id ", nativeQuery = true)
    List<Integer> findByAttentionId(@Param("userId") Integer id);


    void deleteByUserIdAndAttentionId(Integer userId, Integer id);
}
