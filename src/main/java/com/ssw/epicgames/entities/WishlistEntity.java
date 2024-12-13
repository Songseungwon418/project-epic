package com.ssw.epicgames.entities;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"index"})
public class WishlistEntity {
    private int index;
    private String userEmail;
    private int gameIndex;
    private LocalDateTime addAt;
    private boolean isDeleted;

    public WishlistEntity(String userEmail, int gameIndex, LocalDateTime addAt) {
        this.userEmail = userEmail;
        this.gameIndex = gameIndex;
        this.addAt = addAt;
    }
}
