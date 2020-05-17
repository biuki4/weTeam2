package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@IdClass(value = ActivityAttention.class)
@Table(name = "activity_attention")
@Entity
@Data
public class ActivityAttention implements Serializable {

    private static final long serialVersionUID = 437069761884514262L;
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "activity_id")
    private Integer activityId;
}
