package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.GameDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.mappers.GameMapper;
import com.ssw.epicgames.mappers.GenreMapper;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameMapper gameMapper;
    private final GenreMapper genreMapper;

    @Autowired
    public GameService(GameMapper gameMapper, GenreMapper genreMapper) {
        this.gameMapper = gameMapper;
        this.genreMapper = genreMapper;
    }

    public GameDTO getGameDetails(int index) {
        if (index < 1) {
            return null;
        }
        GameEntity game = this.gameMapper.selectGameInfoByIndex(index);
        GameRatingEntity gameRating = this.gameMapper.selectGameRatingByIndex(index);
        MediaEntity[] gameMedia= this.gameMapper.selectGameMediaByIndex(index);
        GenreEntity[] gameGenre = this.gameMapper.selectGameGenreByIndex(index);
        LanguageEntity[] gameLanguage = this.gameMapper.selectGameLanguageByIndex(index);
        CategoryEntity[] gameCategory = this.gameMapper.selectGameCategoryByIndex(index);
        AchievementEntity[] gameAchievement = this.gameMapper.selectGameAchievementByIndex(index);

        return new GameDTO(game, gameRating, gameMedia, gameGenre, gameLanguage, gameCategory, gameAchievement);
    }

    public GameVo[] getAllGames() {
        return this.gameMapper.selectAllGames();
    }

    public GameVo[] getGamesByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = "";
        }
        return this.gameMapper.selectGamesByKeyword(keyword);
    }

    // 게임 index에 해당하는 게임 정보 조회
    public GameEntity getGameByIndex(int index) {
        if (index < 1) {
            return null;
        }
        return this.gameMapper.selectGameInfoByIndex(index);
    }
}
