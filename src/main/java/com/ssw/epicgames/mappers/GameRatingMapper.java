package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.GameRatingEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameRatingMapper {

    /** 등급명으로 등급에 대한 정보 조회 */
    GameRatingEntity selectGameRatingByGrac(String  grac);
}
