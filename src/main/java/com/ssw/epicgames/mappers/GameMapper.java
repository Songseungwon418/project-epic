package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.vos.GameVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameMapper {

    /** 게임 추가(삽입) */
    int insertGame(GameEntity game);

    /** 미디어 추가(삽입) */
    int insertGameMedia(MediaEntity media);

    /** 게임과 미디어 맵핑(삽입) */
    int insertGameMediaMapping(
            @Param("gameIndex")int gameIndex,
            @Param("mediaNo")int mediaNo);

    /** 게임 분류 맵핑 추가(삽입) */
    int insertGameGenreMapping(
            @Param("gameIndex")int gameIndex,
            @Param("tags") List<String> tags
            );

    /** 게임 언어 맵핑 추가(삽입)
     * 자동으로 언어 id를 삽입
     * */
    int insertGameLanguageMapping(
            @Param("gameIndex")int gameIndex,
            @Param("languageIDs") List<Integer> languageIDs
    );

    /** 게임 언어의 id들을 가져옴 */
    List<Integer> selectLanguageId();

    /** 게임 언어 맵핑 추가(삽입)
     * 자동으로 할인 id 홀수나 짝수(넘거주는 값에 따라) 삽입
     * */
    int insertGameDiscountsMapping(
            @Param("gameIndex")int gameIndex,
            @Param("discountsIDs") List<Integer> discountsIDs
    );

    GameVo[] selectAllGames();

    GameVo[] selectGamesByKeyword(@Param("keyword") String keyword);

    /** 게임인덱스로 게임 조회 */
    GameEntity selectGameInfoByIndex(int index);

    /** 모든 게임 조회 */

    List<GameVo> selectSaleGameIndex();

    GameEntity[] selectAllGameInfo();

    GameRatingEntity selectGameRatingByIndex(@Param("index") int index);

    MediaEntity[] selectGameMediaByIndex(@Param("index") int index);

    GenreEntity[] selectGameGenreByIndex(@Param("index") int index);

    LanguageEntity[] selectGameLanguageByIndex(@Param("index") int index);

    CategoryEntity[] selectGameCategoryByIndex(@Param("index") int index);

    AchievementEntity[] selectGameAchievementByIndex(@Param("index") int index);

    List<GameEntity> getGamesByUserEmail(@Param("userEmail") String userEmail);

    PurchaseEntity getPurchaseByEmailAndGameIndex(@Param("userEmail") String userEmail,
                                                  @Param("index") int index);

    // 게임 메인이미지 하나만 조회
    GameEntity selectGameImgByindex(@Param("index") int index);
}
