package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "found")
public class Found implements Serializable {
    private static final long serialVersionUID = 5986269338273608266L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String contact;

    private String url;

    private Integer type;

    private String remark;

    private Date createTime;
}
