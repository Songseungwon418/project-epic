package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(of = {"index"})
public class HomeEntity {
    private int index;
    private String grGrac;
    private String name;
    private int price;
    private String summary;
    private String description;
    private String company;
    private Date openDate;
    private byte[] mainImage;
    private byte[] logo;
    private String min0s;
    private String minCpu;
    private String minRam;
    private String minStorage;
    private String minGpu;
    private String rec0s;
    private String recCpu;
    private String recRam;
    private String recStorage;
    private String recGpu;


    public String getBase64Image() {
        if (mainImage != null) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(mainImage);
        }
        return null;
    }
};

