package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@IdClass(value = UserAttention.class)
@Table(name = "user_attention")
@Entity
public class UserAttention implements Serializable {
    private static final long serialVersionUID = -2560211084773284186L;
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "attention_id")
    private Integer attentionId;
}
