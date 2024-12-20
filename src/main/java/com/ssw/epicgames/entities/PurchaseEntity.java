package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"index"})
public class PurchaseEntity {
    private int index;
    private String userEmail;
    private String payId;
    private int gameIndex;
    private LocalDateTime date;
    private int gamePrice;
    private int discountAmount;
    private int finalPrice;
    private String addr;
    private LocalDateTime deletedAt;
}
