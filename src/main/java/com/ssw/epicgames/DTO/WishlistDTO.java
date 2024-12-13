package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GameRatingEntity;
import com.ssw.epicgames.entities.WishlistEntity;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString
public class WishlistDTO {
    WishlistEntity wishlist;
    GameEntity game;
    GameRatingEntity gameRating;
    boolean duplicateCart; // 장바구니에 이미 있는지
}