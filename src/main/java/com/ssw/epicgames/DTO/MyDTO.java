package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.AchievementEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyDTO extends AchievementEntity {
    private int gameIndex;
    private String gameName;
    private Integer achievementId;
    private String achievementName;
    private int reward;

}
