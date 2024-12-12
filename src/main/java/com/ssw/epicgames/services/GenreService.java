package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.mappers.GenreMapper;

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

}
