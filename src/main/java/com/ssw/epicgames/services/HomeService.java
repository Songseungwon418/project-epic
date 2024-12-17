package com.ssw.epicgames.services;

import com.ssw.epicgames.mappers.HomeMapper;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final HomeMapper homeMapper;

    @Autowired
    public HomeService(HomeMapper homeMapper) {
        this.homeMapper = homeMapper;
    }

    public GameVo[] getNewGames() {
        return this.homeMapper.selectNewGames();
    }

    public GameVo[] getSaleGames() {
        return this.homeMapper.selectSaleGames();
    }
}
