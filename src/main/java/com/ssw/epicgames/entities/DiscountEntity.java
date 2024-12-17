package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class DiscountEntity {
    private int id;
    private String code;
    private float discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
