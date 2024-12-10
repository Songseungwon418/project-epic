package com.ssw.epicgames.DTO;

import com.ssw.epicgames.entities.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 생성
@NoArgsConstructor  // 기본 생성자 생성 (필요한 경우)
@ToString
public class GameDTO {
    private GameEntity game; // 게임 객체
    private GameRatingEntity gameRating; // 게임등급객체
    private MediaEntity[] media; // 게임 이미지 관련 객체
    private GenreEntity[] tag; // 게임 분류
    private LanguageEntity[] language; // 게임 지원 언어
    private CategoryEntity[] category; // 게임 카테고리
    private AchievementEntity[] achievement; // 게임 업적
}
