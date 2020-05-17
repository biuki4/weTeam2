package com.iamk.weTeam.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "game_category")
public class GameCategory implements Serializable {
    private static final long serialVersionUID = -3442794153312043132L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String category;

    private Integer rank;
}
