package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"index"})
public class WishlistEntity {
    private int index;
    private String userEmail;
    private int gameIndex;
    private LocalDateTime addAt;
    private boolean isDeleted;
}
