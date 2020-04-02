package com.iamk.weTeam.model.vo;

import com.iamk.weTeam.model.entity.Game;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class GameUser implements Serializable {

    private Game game;

    private List<Map<String, String>> users = new ArrayList<>();

    public GameUser() {
    }

    public GameUser (Game game, List<Map<String, String>> users) {
        this.game = game;
        this.users = users;
    }
}
