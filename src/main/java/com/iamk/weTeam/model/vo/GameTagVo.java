package com.iamk.weTeam.model.vo;

import com.iamk.weTeam.model.entity.Game;
import lombok.Data;

@Data
public class GameTagVo {
    Game game;
    String tags;
}
