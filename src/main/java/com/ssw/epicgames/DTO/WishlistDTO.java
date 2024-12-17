package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.DiscountEntity;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GameRatingEntity;
import com.ssw.epicgames.entities.WishlistEntity;
import com.ssw.epicgames.vos.PriceVo;
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
    PriceVo price; // 가격 관련된 정보(할인정보 포함)
    boolean duplicateCart; // 장바구니에 이미 있는지
}