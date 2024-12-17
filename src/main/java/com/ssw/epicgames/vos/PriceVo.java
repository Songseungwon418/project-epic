package com.ssw.epicgames.vos;

import com.ssw.epicgames.entities.DiscountEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceVo extends DiscountEntity {
    int originalPrice; // 게임 본래 가격
    int discountPrice; // 할인 가격
    int currentPrice; // 현재 게임 가격(할인되면 할인된 가격, 아님 본래 가격)
    int discountPer; // 할인 퍼센트를 정수로


    /** 게임 본 가격과 할인률을 받아서 실제 가격을 계산 */
    public PriceVo(int originalPrice, float discountPer) {
        this.originalPrice = originalPrice;
        super.setDiscountPercent(discountPer);
        this.discountPer = (int)discountPer;

        // discountPer가 0일 경우, 할인 금액을 0으로 설정
        if (discountPer == 0) {
            this.discountPrice = 0;
            this.currentPrice = originalPrice;  // 할인 없으므로 원 가격 그대로
        } else { // discountPer가 0이 아닐 때 할인 금액 계산
            this.discountPrice = (int)(originalPrice * (discountPer / 100));// originalPrice 에 discountPer(할인률)을 적용하여 할인되는 금액
            this.currentPrice = originalPrice - discountPrice; // 오리지널 가격에 할인 금액을 뺀 가격
        }
    }
}
