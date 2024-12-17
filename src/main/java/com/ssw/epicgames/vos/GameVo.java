package com.ssw.epicgames.vos;

import com.ssw.epicgames.entities.AchievementEntity;
import com.ssw.epicgames.entities.GameEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameVo extends GameEntity {
    private String genre;
}
