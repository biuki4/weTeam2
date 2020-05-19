package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@IdClass(value = ActivityTeamUserUPK.class)
@Table(name = "activity_team_user")
public class ActivityTeamUser implements Serializable{

    private static final long serialVersionUID = -6778029825855000993L;

    @Id
    private Integer teamId;

    @Id
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
