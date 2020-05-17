package com.iamk.weTeam.model.entity;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TeamUserUPK implements Serializable {

    private static final long serialVersionUID = -7124676121396670202L;

    private Integer teamId;

    private Integer userId;
}
