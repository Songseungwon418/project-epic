package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class AchievementEntity {
    private int id;
    private String name;
    private int reward;
    private byte[] logo;

    public String getBase64Logo() {
        if (logo != null) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(logo);
        }
        return null;
    }
}
