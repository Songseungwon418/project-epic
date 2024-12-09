package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.HomeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HomeMapper {
    HomeEntity[] selectNewGames();

    HomeEntity[] selectSaleGames();

}
