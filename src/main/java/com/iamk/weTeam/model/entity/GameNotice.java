package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "game_notice")
public class GameNotice implements Serializable {
    private static final long serialVersionUID = 4584141831398183433L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "notice_info")
    private String noticeInfo;

}