package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.mappers.GameRatingMapper;
import com.ssw.epicgames.mappers.PurchaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseMapper purchaseMapper;
    private final GameRatingMapper gameRatingMapper;

    private final GameService gameService;

    /** 유저에 해당하는 장바구니 목록들 조회 */
    public CartDTO[] getCarts(
//            UserEntity user
            String user
    ) {
        // 유저 유효성 검사(로그인 유뮤)
        if (user == null) {
            return null;
        }
        // 유저의 장바구니 목록들
        CartEntity[] carts = this.purchaseMapper.selectCartByUserEmail(user); //user.getEmail()
        CartDTO[] cartDTOS = new CartDTO[carts.length]; //보낼 객체들 생성
        GameEntity[] games = new GameEntity[carts.length]; // 장바구니 개수만큼 게임 객체 배열 생성
        GameRatingEntity[] gameRatings = new GameRatingEntity[games.length]; // 게임 개수만큼 객체 배열 생성

        // 장바구니에 담긴 게임의 정보 및 등급 저장
        for (int i = 0; i < carts.length; i++) {
            CartEntity cart = carts[i]; // 각 장바구니
            games[i] = this.gameService.getGameByIndex(cart.getGameIndex()); // 각 장바구니에 해당하는 게임 번호로 게임 정보 조회
            System.out.println(games[i].getGrGrac());

            gameRatings[i] = this.gameRatingMapper.selectGameRatingByGrac(games[i].getGrGrac()); // 각 게임에 해당하는 등급 조회
            cartDTOS[i] = CartDTO.builder()
                    .cart(cart)
                    .game(games[i])
                    .gameRating(gameRatings[i])
                    .build(); // 보낼 장바구니 하나씩 집어넣기
        System.out.println(cartDTOS[i]);
        }
        return cartDTOS; // 장바구니들 반환
    }


    /** 유저에 해당하는 위시리스트 목록들 조회 */
    public WishlistDTO[] getWishlists(
    //        UserEntity user
            String user
    ) {
        // 유저 유효성 검사(로그인 유뮤)
        if (user == null) {
            return null;
        }
        // 유저의 장바구니 목록들
        WishlistEntity[] wishlists = this.purchaseMapper.selectWishlistByUserEmail(user); // user.getEmail()
        WishlistDTO[] wishlistDTOS = new WishlistDTO[wishlists.length]; //보낼 객체들 생성
        GameEntity[] games = new GameEntity[wishlists.length]; // 위시리스트 개수만큼 게임 객체 배열 생성
        GameRatingEntity[] gameRatings = new GameRatingEntity[games.length]; // 게임 개수만큼 객체 배열 생성

        // 위시리스트에 담긴 게임의 정보 및 등급 저장
        for (int i = 0; i < wishlists.length; i++) {
            WishlistEntity wishlist = wishlists[i]; // 각 장바구니
            games[i] = this.gameService.getGameByIndex(wishlist.getGameIndex()); // 각 위시리스트에 해당하는 게임 번호로 게임 정보 조회
            gameRatings[i] = this.gameRatingMapper.selectGameRatingByGrac(games[i].getGrGrac()); // 각 게임에 해당하는 등급 조회
            wishlistDTOS[i] = WishlistDTO.builder()
                    .wishlist(wishlist)
                    .game(games[i])
                    .gameRating(gameRatings[i])
                    .build(); // 보낼 위시리스트 하나씩 집어넣기
        }
        return wishlistDTOS; // 위시리스트들 반환
    }
}
