package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GameRatingEntity;
import com.ssw.epicgames.entities.WishlistEntity;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WishlistDTO {
    WishlistEntity wishlist;
    GameEntity game;
    GameRatingEntity gameRating;
}