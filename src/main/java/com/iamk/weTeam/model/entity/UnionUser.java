package com.iamk.weTeam.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "union_user")
public class UnionUser implements Serializable {
    private static final long serialVersionUID = -5829118884320585137L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String name;

    private Integer boundWechat;

    private Integer boundSchoolAccount;

    private Integer type;
}
