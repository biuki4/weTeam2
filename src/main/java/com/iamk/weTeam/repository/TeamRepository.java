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

    @Query(value = "select t.id,t.name,t.contact,t.team_no,t.size,t.brief,g.game_name from team t, game g where t.game_id=g.id and t.user_id=:id", nativeQuery = true)
    List<Map<String, Object>> findByUserId(@Param("id")Integer id);

    // @Query(value = "select t.id,t.name,t.team_no,t.size,t.brief,g.game_name,g.register_start_time,g.register_end_time,g.game_start_time,g.game_end_time from team t, game g where t.game_id=g.id and t.id=:id", nativeQuery = true)
    // List<Map<String, Object>> findTeamGameByTeamId(@Param("id")Integer id);

    @Query(value = "select t.id,t.name,t.contact,t.team_no,t.size,t.brief,g.game_name,tu.type from team t, game g, team_user tu where t.game_id=g.id and t.id=tu.team_id and t.user_id!=:id and tu.user_id=:id", nativeQuery = true)
    List<Map<String, Object>> findParticipateById(@Param("id")Integer id);
    }
