package com.iamk.weTeam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyDTO {
    /**
     * 竞赛
     */
    private Integer gameId;
    /**
     * 队伍id
     */
    private Integer teamId;
    /**
     * 队伍名
     */
    private String tName;
    /**
     * 申请人
     */
    private Integer applyId;
    /**
     * 队长
     */
    private Integer leaderId;
    /**
     * 申请理由
     */
    private String reason;
}
