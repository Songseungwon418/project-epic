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

    public GameVo[] getPopularGames() {
        return this.homeMapper.selectPopularGames();
    }

    public GameVo[] getPlayGames() {
        return this.homeMapper.selectPlayGames();
    }

    public GameVo[] getFreeGames() {
        return this.homeMapper.selectFreeGames();
    }

    public GameVo getGameByIndex(int index, boolean isSale) {
        GameVo[] newGames = this.homeMapper.selectNewGames();
        GameVo[] saleGames = this.homeMapper.selectSaleGames();

        if (isSale) {
            if (index < saleGames.length) {
                return saleGames[index];
            }
        } else {
            if (index < newGames.length) {
                return newGames[index];
            }
        }

        return null;
    }
}
