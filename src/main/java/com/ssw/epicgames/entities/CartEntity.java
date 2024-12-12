package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"index"})
public class CartEntity {
    private int index;
    private String userEmail;
    private int gameIndex;
    private LocalDateTime addAt;
}
