package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "activity")
public class Activity implements Serializable {
    private static final long serialVersionUID = 329146512805786471L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_id")
    private Integer postId;

    private String activityName;

    private String posterUrl;

    private String activityContent;

    private String contact;

    @Temporal(TemporalType.DATE)
    private Date registerStartTime;

    @Temporal(TemporalType.DATE)
    private Date registerEndTime;

    // @Temporal(TemporalType.DATE)
    // private Date activityStartTime;
    //
    // @Temporal(TemporalType.DATE)
    // private Date activityEndTime;

    private String activitySource;

    @OneToOne
    @JoinColumn(name="category_id", referencedColumnName = "id")
    private ActivityCategory activityCategory;

    private String activityUrl;

    private String registerUrl;

    private Integer teamSize;

    @Temporal(TemporalType.DATE)
    private Date postTime;

    @Temporal(TemporalType.DATE)
    private Date modifiedTime;

    private Integer activityViews;

}
