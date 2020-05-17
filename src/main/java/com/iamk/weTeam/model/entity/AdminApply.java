package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "admin_apply")
public class AdminApply implements Serializable {
    private static final long serialVersionUID = 2193061404404584396L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private String contact;

    private String remark;

    private Date createTime;

    private Integer status;
}
