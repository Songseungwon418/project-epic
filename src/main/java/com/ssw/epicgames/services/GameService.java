package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GenreEntity;
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

    public GameVo[] getAllGames() {
        return this.gameMapper.selectAllGames();
    }

    public GameVo[] getGamesByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = "";
        }
        return this.gameMapper.selectGamesByKeyword(keyword);
    }

    public GenreEntity[] getGenres() {
        return this.genreMapper.selectGenres();
    }

    // 게임 index에 해당하는 게임 정보 조회
    public GameEntity getGameByIndex(int index) {
        if (index < 1) {
            return null;
        }
        return this.gameMapper.selectGameInfoByIndex(index);
    }
}
