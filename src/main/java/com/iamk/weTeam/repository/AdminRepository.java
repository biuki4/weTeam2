package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.Admin;
import com.iamk.weTeam.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    @Query(value = "select u.id, u.nickname, u.username, u.gender from user u, admin a where u.id = a.user_id and a.set_id = :userId", nativeQuery = true)
    List<Map<String, Object>> findBySetId(@Param("userId") Integer userId);

    Admin findByUserId(Integer id);
}
