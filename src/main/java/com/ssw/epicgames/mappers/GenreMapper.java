package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.vos.GameVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface GenreMapper {
    GenreEntity[] selectGenres();

    GenreEntity selectGenreByTag(@Param("tag") String tag);

    GameVo[] selectGamesByGenre(@Param("tag") String tag);

    GameVo[] selectGamesByGenreAndKeyword(@Param("tag") String tag,
                                          @Param("keyword") String keyword);
}
