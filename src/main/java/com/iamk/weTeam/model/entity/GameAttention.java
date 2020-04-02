package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@IdClass(value = GameAttention.class)
@Table(name = "game_attention")
@Entity
public class GameAttention implements Serializable {
    private static final long serialVersionUID = 6302092181505598288L;
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "game_id")
    private Integer gameId;
}
