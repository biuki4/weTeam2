package com.iamk.weTeam.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "game")
public class Game implements Serializable {
    private static final long serialVersionUID = -6125692543714546811L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "game_content")
    private String gameContent;


    @Column(name = "post_time")
    private Date postTime;

    @Column(name = "register_start_time")
    private Date registerStartTime;

    @Column(name = "register_end_time")
    private Date registerEndTime;

    @Column(name = "game_start_time")
    private Date gameStartTime;

    @Column(name = "game_end_time")
    private Date gameEndTime;

    @Column(name = "game_source")
    private String gameSource;

    @Column(name = "game_type")
    private Integer gameType;

    @Column(name = "game_url")
    private String gameUrl;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "game_views")
    private Integer gameViews;

    @OneToOne
    @JoinColumn(name="category_id", referencedColumnName = "id")
    private GameCategory gameCategory;

    // game_tag
    // @JsonIgnoreProperties(value = { "gameTags" })
    @JoinTable(name="game_tag_record",
            joinColumns = {@JoinColumn(name="game_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<GameTag> gameTags = new HashSet<>();

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", postId=" + postId +
                ", gameName='" + gameName + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", gameContent='" + gameContent + '\'' +
                ", postTime=" + postTime +
                ", registerStartTime=" + registerStartTime +
                ", registerEndTime=" + registerEndTime +
                ", gameStartTime=" + gameStartTime +
                ", gameEndTime=" + gameEndTime +
                ", gameSource='" + gameSource + '\'' +
                ", gameType=" + gameType +
                ", gameUrl='" + gameUrl + '\'' +
                ", teamSize='" + teamSize + '\'' +
                ", gameViews=" + gameViews +
                ", gameCategory=" + gameCategory +
                ", gameTags=" + gameTags +
                '}';
    }
}
