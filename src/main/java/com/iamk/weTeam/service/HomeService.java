package com.iamk.weTeam.service;

import com.alibaba.fastjson.JSONObject;
import com.iamk.weTeam.model.entity.User;

public interface HomeService {

    void update(User user, JSONObject jsonObject);
}
