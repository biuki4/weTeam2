package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.GameTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameTagRepository extends JpaRepository<GameTag, Integer> {
    GameTag  findByTagName(String str);
}
