package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.mappers.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameMapper gameMapper;

    // 게임 index에 해당하는 게임 정보 조회
    public GameEntity getGameByIndex(int index) {
        if (index < 1) {
            return null;
        }
        return this.gameMapper.selectGameByIndex(index);
    }
}
