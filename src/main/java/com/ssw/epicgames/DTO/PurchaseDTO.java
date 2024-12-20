package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.*;
import lombok.*;

@Value
@Builder
@ToString
public class PurchaseDTO {
    UserEntity user; // 유저 객체
    PayEntity pay; // 결제 내역
    PurchaseEntity[] purchase; //게임 구매 내역들
    GameEntity[] game;
    AchievementEntity[] achievement;
    // 결제 시 필요한 것들 추가 예정
}