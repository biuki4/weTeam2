package com.iamk.weTeam.model.vo;

import com.iamk.weTeam.model.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserVo {
    private List<User> user;
    private String academy;
}
