package com.ssw.epicgames.entities;

import com.ssw.epicgames.enums.GracEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"grac"})
public class GameRatingEntity {
    private GracEnum grac;
    private String title;
    private String subTitle;
    private String description;
}
