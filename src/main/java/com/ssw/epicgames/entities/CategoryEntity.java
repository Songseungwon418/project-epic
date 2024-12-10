package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
@EqualsAndHashCode(of = {"code"})
public class CategoryEntity {
    private String code;
    private String content;
    private byte[] image;

    public String getBase64Image() {
        if (image != null) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(image);
        }
        return null;
    }
}
