package com.ssw.epicgames.mappers;

import com.ssw.epicgames.vos.GameVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HomeMapper {
    GameVo[] selectNewGames();

    GameVo[] selectSaleGames();

    GameVo[] selectPopularGames();

    GameVo[] selectPlayGames();

    GameVo[] selectFreeGames();
}
