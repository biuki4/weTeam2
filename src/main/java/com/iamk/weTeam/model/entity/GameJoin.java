package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@IdClass(value = GameJoin.class)
@Table(name = "game_join")
public class GameJoin implements Serializable {

    private static final long serialVersionUID = -6004743944384170208L;
    @Id
    @Column(name = "game_id")
    private Integer gameId;
    @Id
    @Column(name = "user_id")
    private Integer userId;
    @Id
    @Column(name = "game_team_id")
    private Integer gameTeamId;

    @Override
    public String toString() {
        return "GameJoin{" +
                "userId=" + userId +
                ", gameId=" + gameId +
                ", gameTeamId=" + gameTeamId +
                '}';
    }
}