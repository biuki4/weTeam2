package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "team")
public class Team implements Serializable {
    private static final long serialVersionUID = -5770685499860869578L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer gameId;

    @Temporal(TemporalType.DATE)
    private Date createTime;

    private String teamNo;

    private String name;

    private String contact;

    private String brief;

    private Integer size;

    @Transient
    private User leader;

    // 正式成员
    @Transient
    List<Map<String, Object>> members;

    // 申请成员
    @Transient
    List<Map<String, Object>> applicant;

    @Transient
    private Boolean isMember = false;

    @Transient
    private Boolean isApplicant = false;

}
