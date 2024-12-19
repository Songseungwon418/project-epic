package com.ssw.epicgames.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class PayEntity {
    private String id; // merchant_id(주문번호)
    private String userEmail; // 주문자
    private String impUid; //PortOne 고유 결재 번호, 무료는 결제진행X
    private String name; //주문명
    private Integer amount; //결재 금액
    private String method; //결제수단
    private LocalDateTime paidAt; //결제 일시
    private String pgProvider; //pg사 구분코드(kakaopay, naverpay 등..)
    private String status; //결제 상태
    private String currency; //결재통화(KRW, USD, EUR, JPY)
    private String cardName; //카드사명(삼성카드, 등...)
    private String cardNumber; //카드번호
    private String pgTid; //카드사 고유 결재번호
}
