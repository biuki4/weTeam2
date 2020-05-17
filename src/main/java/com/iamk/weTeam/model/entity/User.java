package com.iamk.weTeam.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties("password")
@Entity
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = -453528825255342992L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nickname;

    private String username;

    @JsonInclude
    private String password;

    private String openId;

    private String unionId;

    private Integer gender;

    private Integer showMe;

    private String grade;

    private Integer academyId;

    private String contact;

    private String phone;

    private String email;

    private String personInfo;

    private String avatarUrl;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.DATE)
    private Date loginLastTime;

    private Integer loginEnable;

    private Integer userViews;

    @Transient
    private Integer userType = 3;

    @Transient      // 非持久化字段
    private String academy;

    /**
     * 是否关注i瓜大， 0-否  1-是
     */
    @Transient
    private Integer isBoundWeChat = 0;

}
