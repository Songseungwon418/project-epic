package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.vos.GameVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final PurchaseService purchaseService;

    @Autowired
    public WishlistService(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    public Pair<Map<Integer, Boolean>, Map<Integer, Integer>> getWishlistData(GameVo[] games, UserEntity user) {
        Map<Integer, Boolean> gameWishlistStatus = new HashMap<>();
        Map<Integer, Integer> gameWishlistIndices = new HashMap<>();

        if (user != null && games != null) {
            WishlistDTO[] wishlists = this.purchaseService.getWishlists(user);

            if (wishlists != null) {
                Set<Integer> wishlistGameIndices = Arrays.stream(wishlists)
                        .map(wishlist -> wishlist.getWishlist().getGameIndex())
                        .collect(Collectors.toSet());

                for (GameVo game : games) {
                    if (game != null && game.getIndex() != 0) {
                        boolean isInWishlist = wishlistGameIndices.contains(game.getIndex());
                        gameWishlistStatus.put(game.getIndex(), isInWishlist);

                        if (isInWishlist) {
                            Integer wishlistIndex = Arrays.stream(wishlists)
                                    .filter(wishlist -> wishlist.getWishlist().getGameIndex() == game.getIndex())
                                    .findFirst()
                                    .map(wishlist -> wishlist.getWishlist().getIndex())
                                    .orElse(null);
                            gameWishlistIndices.put(game.getIndex(), wishlistIndex);
                        }
                    }
                }
            }
        }
        return Pair.of(gameWishlistStatus, gameWishlistIndices);
    }


    public Map<String, Object> getWishlistStatus(UserEntity user, GameVo[] games) {
        Map<Integer, Boolean> gameWishlistStatus = new HashMap<>();
        Map<Integer, Integer> gameWishlistIndices = new HashMap<>();

        if (user != null && games != null) {
            WishlistDTO[] wishlists = this.purchaseService.getWishlists(user);
            if (wishlists != null) {
                Map<Integer, Integer> wishlistMap = Arrays.stream(wishlists)
                        .collect(Collectors.toMap(
                                wishlist -> wishlist.getWishlist().getGameIndex(),
                                wishlist -> wishlist.getWishlist().getIndex()
                        ));

                for (GameVo game : games) {
                    int gameIndex = game.getIndex();
                    gameWishlistStatus.put(gameIndex, wishlistMap.containsKey(gameIndex));
                    if (wishlistMap.containsKey(gameIndex)) {
                        gameWishlistIndices.put(gameIndex, wishlistMap.get(gameIndex));
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("gameWishlistStatus", gameWishlistStatus);
        result.put("gameWishlistIndices", gameWishlistIndices);
        return result;
    }

    public Map<Integer, Boolean> getWishlistStatus(GameVo[] games, WishlistDTO[] wishlists) {
        Map<Integer, Boolean> statusMap = new HashMap<>();
        if (games == null || wishlists == null) {
            return statusMap;
        }

        Set<Integer> wishlistGameIndices = Arrays.stream(wishlists)
                .map(wishlist -> wishlist.getWishlist().getGameIndex())
                .collect(Collectors.toSet());

        for (GameVo game : games) {
            statusMap.put(game.getIndex(), wishlistGameIndices.contains(game.getIndex()));
        }

        return statusMap;
    }

    public Map<Integer, Integer> getWishlistIndices(GameVo[] games, WishlistDTO[] wishlists) {
        Map<Integer, Integer> indexMap = new HashMap<>();
        if (games == null || wishlists == null) {
            return indexMap;
        }

        for (WishlistDTO wishlist : wishlists) {
            indexMap.put(wishlist.getWishlist().getGameIndex(), wishlist.getWishlist().getIndex());
        }

        return indexMap;
    }

    public boolean isInWishlist(int gameIndex, UserEntity user) {
        if (user == null) return false;
        WishlistDTO[] wishlists = this.purchaseService.getWishlists(user);
        if (wishlists == null) return false;

        for (WishlistDTO wishlist : wishlists) {
            if (wishlist.getWishlist().getGameIndex() == gameIndex) {
                return true;
            }
        }
        return false;
    }

    public Integer getWishlistIndex(int gameIndex, UserEntity user) {
        if (user == null) return null;
        WishlistDTO[] wishlists = this.purchaseService.getWishlists(user);
        if (wishlists == null) return null;

        for (WishlistDTO wishlist : wishlists) {
            if (wishlist.getWishlist().getGameIndex() == gameIndex) {
                return wishlist.getWishlist().getIndex();
            }
        }
        return null;
    }
}
