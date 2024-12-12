package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.mappers.GameMapper;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameMapper gameMapper;

    @Autowired
    public GameService(GameMapper gameMapper) {
        this.gameMapper = gameMapper;
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
        return this.gameMapper.selectGenres();
    }
}
