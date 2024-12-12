package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.GenreEntity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface GenreMapper {
    GenreEntity[] selectGenres();
}
