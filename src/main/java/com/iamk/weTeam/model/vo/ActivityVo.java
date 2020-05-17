package com.iamk.weTeam.model.vo;
import com.iamk.weTeam.model.entity.ActivityCategory;

import java.util.Date;

public interface ActivityVo {

    String getActivityName();
    Date getRegisterStartTime();
    Date getRegisterEndTime();
    String getActivitySource();
    ActivityCategory getActivityCategory();
}
