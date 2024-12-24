package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.PayEntity;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@Builder
@ToString
public class PayDTO {
    PayEntity pay; // 결제 내역
    List<PurchaseDTO> purchase; // 구매한 내역과 해당하는 게임 정보들
    int totalGameAmount; // 총 결제 금액(게임 원래 가격)
    int totalDiscount; // 총 할인 금액
    boolean isRefund; // 환불가능여부
}