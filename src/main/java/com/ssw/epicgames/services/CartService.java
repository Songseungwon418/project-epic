package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final PurchaseService purchaseService;

    @Autowired
    public CartService(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    public boolean isInCart(int gameIndex, UserEntity user) {
        if (user == null) return false;
        CartDTO[] carts = this.purchaseService.getCarts(user);
        if (carts == null) return false;

        for (CartDTO cart : carts) {
            if (cart.getCart().getGameIndex() == gameIndex) {
                return true;
            }
        }
        return false;
    }

    public Integer getCartIndex(int gameIndex, UserEntity user) {
        if (user == null) return null;
        CartDTO[] carts = this.purchaseService.getCarts(user);
        if (carts == null) return null;

        for (CartDTO cart : carts) {
            if (cart.getCart().getGameIndex() == gameIndex) {
                return cart.getCart().getIndex();
            }
        }
        return null;
    }
}
