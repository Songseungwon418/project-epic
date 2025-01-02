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
}
