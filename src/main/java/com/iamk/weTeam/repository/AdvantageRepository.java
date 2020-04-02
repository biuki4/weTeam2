package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.Advantage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvantageRepository extends JpaRepository<Advantage, Integer> {
    List<Advantage> findAllByUserId(Integer id);
}
