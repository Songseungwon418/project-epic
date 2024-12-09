package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(of = {"index"})
public class HomeEntity {
    private int index;
    private String name;
    private int price;
    private Date openDate;
    private byte[] mainImage;
    private int discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;

    public String getBase64Image() {
        if (mainImage != null) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(mainImage);
        }
        return null;
    }
};

