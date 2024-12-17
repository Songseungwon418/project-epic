package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.CartEntity;
import com.ssw.epicgames.entities.DiscountEntity;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GameRatingEntity;
import com.ssw.epicgames.vos.PriceVo;
import lombok.*;

@Value
@Builder
@ToString
public class CartDTO {
    CartEntity cart; // 장바구니 정보
    GameEntity game; // 게임 정보
    GameRatingEntity gameRating; // 게임 분류
    PriceVo price; // 가격 관련된 정보(할인정보 포함)
    int discountTotalPrice; // 장바구니에 있는 게임의 총 할인 가격
    int totalPrice; // 장바구니에 있는 게임의 총 가격
    boolean duplicateWishlist; // 위시리스트에 이미 있는지
}