package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.mappers.GenreMapper;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenreService {

    private final GenreMapper genreMapper;

    @Autowired
    public GenreService(GenreMapper genreMapper) {
        this.genreMapper = genreMapper;
    }

    public GenreEntity[] getGenres() {
        return this.genreMapper.selectGenres();
    }

    public GenreEntity getGenreByTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        return this.genreMapper.selectGenreByTag(tag);
    }

    public GameVo[] getGamesByGenre(String tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        return this.genreMapper.selectGamesByGenre(tag);
    }

    public GameVo[] getGamesByGenreAndKeyword(String tag, String keyword) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        if (keyword == null || keyword.isEmpty()) {
            keyword = "";
        }
        return this.genreMapper.selectGamesByGenreAndKeyword(tag, keyword);
    }
}
