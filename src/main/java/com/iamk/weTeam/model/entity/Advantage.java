package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "advantage")
public class Advantage implements Serializable {

    private static final long serialVersionUID = 5082948920138839527L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    @Column(name = "name")
    private String name;

    @Column(name = "brief")
    private String brief;

    @Override
    public String toString() {
        return "Advantage{" +
                "id=" + id +
                ", userId=" + userId +
                ", name=" + name +
                ", brief='" + brief + '\'' +
                '}';
    }
}
