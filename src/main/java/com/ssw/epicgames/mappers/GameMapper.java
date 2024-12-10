package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.GameEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameMapper {

    /** 모든 게임들 */
    GameEntity[] selectAllGames();

    /** index에 해당하는 게임 한 개 */
    GameEntity selectGameByIndex(int index);
}
