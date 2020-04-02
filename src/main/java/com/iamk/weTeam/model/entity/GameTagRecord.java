package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@IdClass(value = GameTagRecord.class)
@Table(name = "game_tag_record")
public class GameTagRecord implements Serializable {
    private static final long serialVersionUID = -4745766674992779468L;
    @Id
    @Column(name = "game_id")
    private Integer gameId;

    @Id
    @Column(name = "tag_id")
    private Integer tagId;

}