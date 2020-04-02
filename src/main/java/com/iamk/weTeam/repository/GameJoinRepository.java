package com.iamk.weTeam.repository;


import com.iamk.weTeam.model.entity.GameJoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameJoinRepository extends JpaRepository<GameJoin, Integer> {
    List<GameJoin> findByUserId(int userId);


}
