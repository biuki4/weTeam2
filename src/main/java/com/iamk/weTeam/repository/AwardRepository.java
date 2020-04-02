package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Integer> {

    List<Award> findAllByUserId(Integer id);
}
