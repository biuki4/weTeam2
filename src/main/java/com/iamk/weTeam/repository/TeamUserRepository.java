package com.iamk.weTeam.repository;

import com.iamk.weTeam.model.entity.TeamUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Integer> {
    @Query(value = "select count(tu.team_id) from team_user tu where tu.team_id=:id and tu.type=:uType", nativeQuery = true)
    Integer countTeamMember(@Param("id") Integer id, @Param("uType") Integer uType);

    TeamUser findByTeamIdAndUserId(Integer id, Integer userId);

    @Transactional
    @Modifying
    @Query(value = " delete from team_user where team_user.team_id=:id", nativeQuery = true)
    void deleteByTeamId(@Param("id")Integer id);

    @Transactional
    @Modifying
    @Query(value = "delete from team_user where team_user.team_id=:id and team_user.user_id=:userId", nativeQuery = true)
    void deleteByTeamIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);

    @Query(value = "select count(t.id) from team t, team_user tu where t.id = tu.team_id and t.game_id=:gameId and tu.user_id=:userId and tu.type=:uType", nativeQuery = true)
    Integer countUser(@Param("gameId") Integer gameId,@Param("userId") Integer userId, @Param("uType") Integer uType);
}
