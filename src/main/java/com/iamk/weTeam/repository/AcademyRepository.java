package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.Academy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AcademyRepository extends JpaRepository<Academy, Integer> {

    @Query(value = "select a.id, a.name from academy a", nativeQuery = true)
    HashMap<Integer, String> findAllMap();
}
