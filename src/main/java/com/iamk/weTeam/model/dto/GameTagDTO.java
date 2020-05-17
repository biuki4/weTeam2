package com.iamk.weTeam.model.dto;

import com.iamk.weTeam.model.entity.Game;
import lombok.Data;

@Data
public class GameTagDTO {
    Game game;
    String tags;
}
