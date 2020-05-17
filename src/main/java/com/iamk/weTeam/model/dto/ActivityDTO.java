package com.iamk.weTeam.model.dto;

import lombok.Data;

/**
 * 多条件复杂查询活动
 */
@Data
public class ActivityDTO {
    /**
     * 关键字
     */
    private String key;
    /**
     * 分类id
     */
    private Integer categoryId;
    /**
     * 活动主办方
     */
    private String source;
    /**
     * 时间
     */
    private Integer time;
}
