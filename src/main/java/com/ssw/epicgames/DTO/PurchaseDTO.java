package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.vos.PriceVo;
import lombok.*;

@Value
@Builder
@ToString
public class PurchaseDTO {
    PurchaseEntity purchase; //게임 구매 내역
    GameEntity game; // 해당 게임 정보
    PriceVo price; // 할인 정보g
}