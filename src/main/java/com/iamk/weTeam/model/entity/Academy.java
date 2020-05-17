package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "academy")
public class Academy implements Serializable {
    private static final long serialVersionUID = -2848563742228751425L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer rank;
}
