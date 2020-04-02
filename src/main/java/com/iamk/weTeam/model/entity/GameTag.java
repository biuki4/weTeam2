package com.iamk.weTeam.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "game_tag")
public class GameTag implements Serializable {
    private static final long serialVersionUID = -8337201167373361135L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tag_name")
    private String tagName;

    @JsonIgnore
    // @JsonIgnoreProperties(value = { "games" })
    @ManyToMany(mappedBy = "gameTags",fetch = FetchType.EAGER)
    private Set<Game> games = new HashSet<>();

    @Override
    public String toString() {
        return "GameTag{" +
                "id=" + id +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}