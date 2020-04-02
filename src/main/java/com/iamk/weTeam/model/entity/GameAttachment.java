package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "game_attachment")
public class GameAttachment implements Serializable {
    private static final long serialVersionUID = -2298853383528529894L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "upload_time")
    private Date uploadTime;

}