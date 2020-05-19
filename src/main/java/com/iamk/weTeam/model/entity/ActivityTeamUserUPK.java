package com.iamk.weTeam.model.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ActivityTeamUserUPK implements Serializable {
    private static final long serialVersionUID = -1618935281506867542L;

    private Integer teamId;

    private Integer userId;

}
