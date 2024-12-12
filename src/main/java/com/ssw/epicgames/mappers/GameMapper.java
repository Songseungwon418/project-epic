package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.vos.GameVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameMapper {

    GameVo[] selectAllGames();

    GameVo[] selectGamesByKeyword(@Param("keyword") String keyword);

    GameEntity selectGameInfoByIndex(int index);

    GameEntity[] selectAllGameInfo();
}
