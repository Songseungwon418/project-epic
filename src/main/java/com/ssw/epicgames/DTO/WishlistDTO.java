package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GameRatingEntity;
import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.entities.WishlistEntity;
import com.ssw.epicgames.vos.PriceVo;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString
public class WishlistDTO{
    WishlistEntity wishlist;
    GameEntity game;
    GameRatingEntity gameRating;
    String tag; // 게임 분류
    PriceVo price; // 가격 관련된 정보(할인정보 포함)
    boolean duplicateCart; // 장바구니에 이미 있는지
}