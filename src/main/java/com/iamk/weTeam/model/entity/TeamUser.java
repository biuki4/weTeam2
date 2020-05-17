package com.iamk.weTeam.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@IdClass(value = TeamUserUPK.class)
@Table(name = "team_user")
public class TeamUser implements Serializable {

    private static final long serialVersionUID = 449177401561309046L;

    @Id
    @Column(name = "team_id")
    private Integer teamId;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    private Integer type;

    private String remark;

    private Date time;

    private Integer cancelNum;

    private Integer sendStatus;

    private String sendLog;

    public void init_log_status() {
        this.sendLog = "-1";
        this.sendStatus = 0;
    }

    public void init_log_status_cancel() {
        this.sendLog = "-1";
        this.sendStatus = 0;
        this.cancelNum = 0;
    }

}
