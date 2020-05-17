package com.iamk.weTeam.model.vo;

import com.iamk.weTeam.model.entity.GameCategory;

import java.util.Date;

public interface GameVo {

    String getGameName();
    Date getRegisterStartTime();
    Date getRegisterEndTime();
    Date getGameStartTime();
    Date getGameEndTime();
    String getGameSource();
    String getGameType();
    GameCategory getGameCategory();
}
