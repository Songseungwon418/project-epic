package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.*;
import lombok.*;

@Getter
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 생성
@NoArgsConstructor  // 기본 생성자 생성 (필요한 경우)
@ToString
public class PurchaseDTO {
    private UserEntity user; // 유저 객체
    private PurchaseEntity[] purchase; //결제한 정보들
    // 결제 시 필요한 것들 추가 예정
}