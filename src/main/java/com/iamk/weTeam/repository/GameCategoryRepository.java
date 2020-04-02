package com.iamk.weTeam.repository;


import com.iamk.weTeam.model.entity.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameCategoryRepository extends JpaRepository<GameCategory, Integer> {

}
