package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.HomeEntity;
import com.ssw.epicgames.mappers.HomeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final HomeMapper homeMapper;

    @Autowired
    public HomeService(HomeMapper homeMapper) {
        this.homeMapper = homeMapper;
    }

    public HomeEntity[] getNewGames() {
        return this.homeMapper.selectNewGames();
    }

    public HomeEntity[] getSaleGames() {
        return this.homeMapper.selectSaleGames();
    }
}
