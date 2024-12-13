package com.ssw.epicgames.entities;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"index"})
public class CartEntity {
    private int index;
    private String userEmail;
    private int gameIndex;
    private LocalDateTime addAt;

    public CartEntity(String userEmail, int gameIndex, LocalDateTime addAt) {
        this.userEmail = userEmail;
        this.gameIndex = gameIndex;
        this.addAt = addAt;
    }
}
