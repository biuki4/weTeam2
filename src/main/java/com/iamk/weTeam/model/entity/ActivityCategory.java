package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "activity_category")
public class ActivityCategory implements Serializable {

    private static final long serialVersionUID = -5260121046338209595L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String category;

    private Integer rank;
}
