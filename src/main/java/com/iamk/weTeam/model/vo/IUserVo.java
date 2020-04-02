package com.iamk.weTeam.model.vo;

import java.util.Date;

public interface IUserVo {

    Integer getId();
    String getNickname();
    String getUsername();
    Integer getGender();
    Integer getUserType();
    String getGrade();
    String getAcademy();
    String getPhone();
    String getEmail();
    String getPersonInfo();
    String getAvatarUrl();
    Date getLoginLastTime();
    Integer getUserViews();

}
