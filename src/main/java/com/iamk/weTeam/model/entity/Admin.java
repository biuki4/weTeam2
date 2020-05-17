package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "admin")
public class Admin implements Serializable {
    private static final long serialVersionUID = -8436444094026862692L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer setId;

    private Integer userType;

    // private Date createTime;

    private String email;

    @Column(name = "_password")
    private String password;

    private String comment;
}
