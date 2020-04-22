package com.iamk.weTeam.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
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

    private Integer gender;

    @Transient
    private Integer userType = 3;

    private Integer showMe;

    private String grade;

    private Integer academyId;

    @Transient      // 非持久化字段
    private String academy;

    private String contact;

    private String phone;

    private String email;

    @Column(name = "person_info")
    private String personInfo;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Temporal(TemporalType.DATE)
    @Column(name = "create_time")
    private Date createTime;

    @Temporal(TemporalType.DATE)
    @Column(name = "login_last_time")
    private Date loginLastTime;

    @Column(name = "login_enable")
    private Integer loginEnable;

    private Integer userViews;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", openId='" + openId + '\'' +
                ", gender=" + gender +
                ", userType=" + userType +
                ", showMe=" + showMe +
                ", grade='" + grade + '\'' +
                ", academyId=" + academyId +
                ", academy='" + academy + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", personInfo='" + personInfo + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", createTime=" + createTime +
                ", loginLastTime=" + loginLastTime +
                ", loginEnable=" + loginEnable +
                ", userViews=" + userViews +
                '}';
    }
}
