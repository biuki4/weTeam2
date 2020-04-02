package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.Team;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByGameId(Integer id, Sort createTime);

    @Query(value = "select u.id, u.nickname, u.gender, u.avatar_url as avatarUrl from user u, team_user tu where u.id=tu.user_id and tu.team_id=:teamId and tu.type=:uType", nativeQuery = true)
    List<Map<String, Object>> findTeamMember(@Param("teamId") Integer teamId, @Param("uType") Integer uType);

    Team findByUserIdAndGameId(Integer userId, Integer id);
}
