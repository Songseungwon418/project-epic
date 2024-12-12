package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
@EqualsAndHashCode(of = {"no"})
public class MediaEntity {
    private int no;
    private byte[] image;
    private byte[] video;

    public String getBase64Image() {
        if (image != null) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(image);
        }
        return null;
    }
}
