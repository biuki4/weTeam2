package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.UnionUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnionUserRepository extends JpaRepository<UnionUser, Integer> {
    UnionUser findByUserId(Integer id);
}
