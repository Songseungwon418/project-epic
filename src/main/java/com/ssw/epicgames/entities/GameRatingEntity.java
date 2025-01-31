package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
@EqualsAndHashCode(of = {"grac"})
public class GameRatingEntity {
    private String grac;
    private String title;
    private String subTitle;
    private String description;
    private byte[] logo;

    public String getBase64Logo() {
        if (logo != null) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(logo);
        }
        return null;
    }
}
