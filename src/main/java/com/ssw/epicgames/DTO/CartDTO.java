package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.CartEntity;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GameRatingEntity;
import lombok.*;

@Value
@Builder
@ToString
public class CartDTO {
    CartEntity cart;
    GameEntity game;
    GameRatingEntity gameRating;
    boolean duplicateWishlist; // 위시리스트에 이미 있는지
}