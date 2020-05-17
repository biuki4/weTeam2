package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.ActivityCategory;
import com.iamk.weTeam.model.entity.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Integer> {

}
