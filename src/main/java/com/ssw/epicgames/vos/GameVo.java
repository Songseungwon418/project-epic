package com.ssw.epicgames.vos;

import com.ssw.epicgames.entities.AchievementEntity;
import com.ssw.epicgames.entities.GameEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GameVo extends GameEntity {
    private String genre;
    private int discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
