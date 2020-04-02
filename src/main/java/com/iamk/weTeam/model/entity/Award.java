package com.iamk.weTeam.model.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "award")
public class Award implements Serializable {
    private static final long serialVersionUID = 6452798407567737209L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String name;

    private String type;

    private String brief;

    @Column(name = "paper_url")
    private String paperUrl;

    @Override
    public String toString() {
        return "Award{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", brief='" + brief + '\'' +
                ", paperUrl='" + paperUrl + '\'' +
                '}';
    }
}
