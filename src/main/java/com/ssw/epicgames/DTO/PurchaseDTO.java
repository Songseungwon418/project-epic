package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.*;
import lombok.*;

@Value
@Builder
@ToString
public class PurchaseDTO {
    UserEntity user; // 유저 객체
    PurchaseEntity[] purchase; //결제한 정보들
    // 결제 시 필요한 것들 추가 예정
}