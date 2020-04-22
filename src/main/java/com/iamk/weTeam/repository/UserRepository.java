package com.iamk.weTeam.repository;


import com.iamk.weTeam.model.entity.User;
import com.iamk.weTeam.model.vo.IUserVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface UserRepository extends JpaRepository<User, Integer>
        // , JpaSpecificationExecutor<User>
{

    User findByUsername(String userName);

    // @Query(value = "select u.id,u.avatar_url as avatarUrl from game_join gj, `user` u where gj.user_id = u.id and gj.game_team_id = :teamId and game_id = :gameId", nativeQuery = true)
    // List<Map<String, String>> findTeamMember(@Param("teamId") Integer teamId, @Param("gameId") Integer gameId);

    @Query(value = "select u.id,u.nickname,u.avatar_url as avatarUrl,u.academy,u.grade,u.username from user u where u.id=:userId", nativeQuery = true)
    Map<String, Object> findBaseById(@Param("userId") Integer userId);

    User findByOpenId(String openid);

    User findByUsernameAndPassword(String userName, String password);

    List<User> findByIdIn(List<Integer> ids);

    List<IUserVo> findByShowMe(Integer i, Pageable pageable);

    List<User> findAll(Specification<User> spec, Pageable pageable);

    @Query(value = "select u.id from user u where u.open_id = :openId", nativeQuery = true)
    Integer findIdByOpenId(String openId);

}
