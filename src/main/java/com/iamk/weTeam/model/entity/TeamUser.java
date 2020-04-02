package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@IdClass(value = TeamUser.class)
@Table(name = "team_user")
public class TeamUser implements Serializable {

    private static final long serialVersionUID = 449177401561309046L;
    @Id
    @Column(name = "team_id")
    private Integer teamId;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    private Integer type;
}
