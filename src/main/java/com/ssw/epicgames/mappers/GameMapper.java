package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.vos.GameVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameMapper {

    GameVo[] selectAllGames();

    GameVo[] selectGamesByKeyword(@Param("keyword") String keyword);

    GameEntity selectGameInfoByIndex(int index);

    GameEntity[] selectAllGameInfo();

    GameRatingEntity selectGameRatingByIndex(@Param("index") int index);

    MediaEntity[] selectGameMediaByIndex(@Param("index") int index);

    GenreEntity[] selectGameGenreByIndex(@Param("index") int index);

    LanguageEntity[] selectGameLanguageByIndex(@Param("index") int index);

    CategoryEntity[] selectGameCategoryByIndex(@Param("index") int index);

    AchievementEntity[] selectGameAchievementByIndex(@Param("index") int index);

    List<GameEntity> getGamesByUserEmail(@Param("userEmail") String userEmail);
}
