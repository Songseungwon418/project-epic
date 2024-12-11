package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.exceptions.TransactionalException;
import com.ssw.epicgames.mappers.GameRatingMapper;
import com.ssw.epicgames.mappers.PurchaseMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.resutls.purchase.PurchaseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseMapper purchaseMapper;
    private final GameRatingMapper gameRatingMapper;

    private final GameService gameService;

    //region 장바구니 관련
    /** 유저에 해당하는 장바구니 목록들 조회 */
    public CartDTO[] getCarts(UserEntity user) {
        // 유저 유효성 검사(로그인 유뮤)
        if (user == null) {
            return null;
        }
        // 유저의 장바구니 목록들
        CartEntity[] carts = this.purchaseMapper.selectCartByUserEmail(user.getEmail());
        CartDTO[] cartDTOS = new CartDTO[carts.length]; //보낼 객체들 생성
        GameEntity[] games = new GameEntity[carts.length]; // 장바구니 개수만큼 게임 객체 배열 생성
        GameRatingEntity[] gameRatings = new GameRatingEntity[games.length]; // 게임 개수만큼 객체 배열 생성

        // 장바구니에 담긴 게임의 정보 및 등급 저장
        for (int i = 0; i < carts.length; i++) {
            CartEntity cart = carts[i]; // 각 장바구니
            games[i] = this.gameService.getGameByIndex(cart.getGameIndex()); // 각 장바구니에 해당하는 게임 번호로 게임 정보 조회
            gameRatings[i] = this.gameRatingMapper.selectGameRatingByGrac(games[i].getGrGrac()); // 각 게임에 해당하는 등급 조회
            cartDTOS[i] = CartDTO.builder()
                    .cart(cart)
                    .game(games[i])
                    .gameRating(gameRatings[i])
                    .build(); // 보낼 장바구니 하나씩 집어넣기
        }
        return cartDTOS; // 장바구니들 반환
    }

    /** 유저에 해당하는 장바구니 목록 삽입 */
    @Transactional
    public Result addToCart(CartEntity cart) {
        if (cart == null || cart.getUserEmail() == null || cart.getIndex() < 1 || cart.getGameIndex() < 1) {
            return CommonResult.FAILURE;
        }

        CartEntity dbCart = this.purchaseMapper.selectCartByEmailANDGameIndex(cart.getUserEmail(), cart.getGameIndex());
        if (dbCart != null) { //장바구니에 이미 있음
            return PurchaseResult.FAILURE_DUPLICATE_CART;
        }
        cart.setAddAt(LocalDateTime.now());

        if(this.purchaseMapper.insertCart(cart) <= 0) {
            throw new TransactionalException("오류: 장바구니 삽입 실패");
        }
        return CommonResult.SUCCESS;
    }

    /** 장바구니 제거 */
    @Transactional
    public Result deleteFromCart(UserEntity user, int gameIndex, int index) {
        if (user == null || gameIndex < 1 || index < 1) {
            return CommonResult.FAILURE;
        }
        // 유저 이메일과 게임 번호로 장바구니 조회
        CartEntity dbCart = this.purchaseMapper.selectCartByEmailANDGameIndex(user.getEmail(), gameIndex);
        //장바구니에 없으면
        if (dbCart == null) {
            return PurchaseResult.FAILURE_NOT_FOUND;
        }
        // 장바구니에 있으면 장바구니 삭제 진행
        if (this.purchaseMapper.deleteCartByIndex(index) <= 0){
            throw new TransactionalException("오류: 장바구니 삭제 실패");
        }

        return CommonResult.SUCCESS;
    }

    //endregion

    //region 위시리스트 관련
    /** 유저에 해당하는 위시리스트 목록들 조회 */
    public WishlistDTO[] getWishlists(UserEntity user) {
        // 유저 유효성 검사(로그인 유뮤)
        if (user == null) {
            return null;
        }
        // 유저의 위시리스트 목록들
        WishlistEntity[] wishlists = this.purchaseMapper.selectWishlistByUserEmail(user.getEmail());
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

    /** 유저에 해당하는 위시리스트 목록 삽입 */
    @Transactional
    public Result addToWishlist(WishlistEntity wishlist) {
        if (wishlist == null || wishlist.getUserEmail() == null || wishlist.getIndex() < 1 || wishlist.getGameIndex() < 1) {
            return CommonResult.FAILURE;
        }

        WishlistEntity dbWishlist = this.purchaseMapper.selectWishlistByEmailANDGameIndex(wishlist.getUserEmail(), wishlist.getGameIndex());
        if (dbWishlist != null || !dbWishlist.isDeleted()) { //위시리스트에 이미 있음(삭제도 안됨)
            return PurchaseResult.FAILURE_DUPLICATE_CART;
        }
        wishlist.setAddAt(LocalDateTime.now());

        if(this.purchaseMapper.insertWishlist(wishlist) <= 0) {
            throw new TransactionalException("오류: 위시리스트 삽입 실패");
        }
        return CommonResult.SUCCESS;
    }

    /** 위시리스트 제거 */
    @Transactional
    public Result deleteFromWishlist(WishlistEntity wishlist) {
        if (wishlist == null || wishlist.getUserEmail() == null || wishlist.getIndex() < 1 || wishlist.getGameIndex() < 1
        ) {
            return CommonResult.FAILURE;
        }
        WishlistEntity dbWishlist = this.purchaseMapper.selectWishlistByEmailANDGameIndex(wishlist.getUserEmail(), wishlist.getGameIndex());
        if (dbWishlist == null || dbWishlist.isDeleted()) { //위시리스트에 없거나 삭제됨
            return PurchaseResult.FAILURE_NOT_FOUND;
        }

        wishlist.setDeleted(true); // 삭제됨
        if (this.purchaseMapper.updateWishlistByIndex(wishlist) <= 0){
            throw new TransactionalException("오류: 위시리스트 삭제 실패");
        }
        return CommonResult.SUCCESS;
    }
    //endregion

}