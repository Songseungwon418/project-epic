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
import com.ssw.epicgames.vos.GameVo;
import com.ssw.epicgames.vos.PriceVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseMapper purchaseMapper;
    private final GameRatingMapper gameRatingMapper;

    private final GameService gameService;
    private final PriceService priceService;

    //region 장바구니 관련
    /** 유저에 해당하는 장바구니 목록들 조회 */
    public CartDTO[] getCarts(UserEntity user) {
        // 유저 유효성 검사(로그인 유뮤)
        if (user == null) {
            return null;
        }

        // 유저의 장바구니 목록들
        CartEntity[] carts = this.purchaseMapper.selectCartByUserEmail(user.getEmail());
        if (carts == null) {
            return null;
        }else {
            CartDTO[] cartDTOS = new CartDTO[carts.length]; //보낼 객체들 생성
            GameEntity[] games = new GameEntity[carts.length]; // 장바구니 개수만큼 게임 객체 배열 생성
            GameRatingEntity[] gameRatings = new GameRatingEntity[games.length]; // 게임 개수만큼 객체 배열 생성
            int discountTotalPrice = 0; // 장바구니에 존재하는 게임들의 할인 가격 총합
            int totalPrice = 0; // 장바구니에 존재하는 게임들의 가격의 총합

            // 장바구니에 담긴 게임의 정보 및 등급 저장
            for (int i = 0; i < carts.length; i++) {
                CartEntity cart = carts[i]; // 각 장바구니
                games[i] = this.gameService.getGameByIndex(cart.getGameIndex()); // 각 장바구니에 해당하는 게임 번호로 게임 정보 조회
                gameRatings[i] = this.gameRatingMapper.selectGameRatingByGrac(games[i].getGrGrac()); // 각 게임에 해당하는 등급 조회

                // 해당하는 게임의 할인에 관련된 가격 정보를 가져옴
                PriceVo price = priceService.discountInfo(games[i].getIndex(), games[i].getPrice());

                // 누적해서 총 할인 가격, 맨 마지막에 설정한 CartDTO 가 최종 가격임
                if(price.getDiscountPrice() > 0) { // 할인 금액이 0이 아닌 경우(할인 가격이 있음)
                    discountTotalPrice += price.getDiscountPrice();
                }

                // 누적해서 총 가격, , 맨 마지막에 설정한 CartDTO 가 최종 가격임
                totalPrice += price.getCurrentPrice();

                // 보낼 데이터 설정
                cartDTOS[i] = CartDTO.builder()
                        .cart(cart) // 장바구니 정보
                        .game(games[i]) // 게임 정보
                        .gameRating(gameRatings[i]) // 게임 분류
                        .price(price) // 가격 정보
                        .discountTotalPrice(discountTotalPrice) // 총 할인 가격
                        .totalPrice(totalPrice) // 총 가격
                        .duplicateWishlist(DuplicationCheckWishlist(user.getEmail(), games[i].getIndex()))// 위시리스트에 있음 true
                        .build(); // 보낼 장바구니 하나씩 집어넣기
            }
            return cartDTOS; // 장바구니들 반환
        }
    }

    /** 유저에 해당하는 장바구니 목록 삽입 */
    @Transactional
    public Result addToCart(UserEntity user, int gameIndex) {
        if (user == null || user.getEmail() == null || gameIndex < 1 ) {
            return CommonResult.FAILURE;
        }
        // 장바구니에 존재하는 지 중복 체크
        if(DuplicationCheckCart(user.getEmail(), gameIndex)) {
            return PurchaseResult.FAILURE_DUPLICATE_CART;
        }
        // 장바구니에 없다면 CartEntity 생성해서 값 셋팅
        CartEntity cart = new CartEntity(user.getEmail(), gameIndex, LocalDateTime.now());
        // db에 삽입
        if(this.purchaseMapper.insertCart(cart) <= 0) {
            throw new TransactionalException("오류: 장바구니 삽입 실패");
        }
        return CommonResult.SUCCESS;
    }

    // 장바구니에서 위시리스트로 이동 눌렀을 경우
    @Transactional
    public Result addToWishlist(UserEntity user, int gameIndex, int index, String userEmail) {
        if (user == null || user.getEmail() == null || gameIndex < 1 || index < 1 ||
                userEmail == null || userEmail.isEmpty() || !userEmail.equals(user.getEmail())) {
            return CommonResult.FAILURE;
        }

        // 요청한 index가 실제로 장바구니에 있는 index인지 확인
        CartEntity dbCart = this.purchaseMapper.selectCartByIndex(index);
        if (dbCart == null) {
            return PurchaseResult.FAILURE_NOT_FOUND; // 존재하지않는 index
        }

        // 위시리스트에 이동하기 전에 위시리스트에 있는지 중복 확인
        if (DuplicationCheckWishlist(user.getEmail(), gameIndex)) {
            return PurchaseResult.FAILURE_DUPLICATE_WISHLIST; // 위시리스트에 이미 있음
        }

        // 위시리스트에 없을 경우, 데이터를 담을 객체 생성하여 셋팅
        WishlistEntity wishlist = new WishlistEntity(user.getEmail(), gameIndex, LocalDateTime.now());
        // DB에 삽입
        if(this.purchaseMapper.insertWishlist(wishlist) <= 0) {
            throw new TransactionalException("오류: 위시리스트 삽입 실패");
        }
        return CommonResult.SUCCESS;
    }

    /** 장바구니 제거 */
    @Transactional
    public Result deleteFromCart(int index) {
        if ( index < 1) {
            return CommonResult.FAILURE;
        }

        // 장바구니 조회
        CartEntity dbCart = this.purchaseMapper.selectCartByIndex(index);

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
            WishlistEntity wishlist = wishlists[i]; // 각 위시리스트
            games[i] = this.gameService.getGameByIndex(wishlist.getGameIndex()); // 각 위시리스트에 해당하는 게임 번호로 게임 정보 조회
            gameRatings[i] = this.gameRatingMapper.selectGameRatingByGrac(games[i].getGrGrac()); // 각 게임에 해당하는 등급 조회

            // 게임의 할인 정보를 가져옴
            PriceVo price = priceService.discountInfo(games[i].getIndex(), games[i].getPrice());

            wishlistDTOS[i] = WishlistDTO.builder()
                    .wishlist(wishlist)
                    .game(games[i])
                    .gameRating(gameRatings[i])
                    .price(price)
                    .duplicateCart(DuplicationCheckCart(user.getEmail(), games[i].getIndex())) // 장바구니 존재하면 true
                    .build(); // 보낼 위시리스트 하나씩 집어넣기
        }
        return wishlistDTOS; // 위시리스트들 반환
    }

    /** 유저에 해당하는 위시리스트 목록 삽입 */
    @Transactional
    public Result addToWishlist(UserEntity user, int gameIndex) {
        if (user == null || user.getEmail() == null || gameIndex < 1 ) {
            return CommonResult.FAILURE;
        }

        //위시리스트에 이미 있음(삭제도 안됨)
        if (DuplicationCheckWishlist(user.getEmail(), gameIndex)) {
            return PurchaseResult.FAILURE_DUPLICATE_CART;
        }
        WishlistEntity wishlist = new WishlistEntity(user.getEmail(), gameIndex, LocalDateTime.now());

        if(this.purchaseMapper.insertWishlist(wishlist) <= 0) {
            throw new TransactionalException("오류: 위시리스트 삽입 실패");
        }
        return CommonResult.SUCCESS;
    }

    // 위시리스트에서 장바구니에 담기 눌렀을 경우
    @Transactional
    public Result addToCart(UserEntity user, int gameIndex, int index, String userEmail) {
        if (user == null || user.getEmail() == null || gameIndex < 1 || index < 1 ||
                userEmail == null || userEmail.isEmpty() || !userEmail.equals(user.getEmail())) {
            return CommonResult.FAILURE;
        }

        // 실제로 위시리스트에 존재하는 index 확인
        WishlistEntity dbWishlist = this.purchaseMapper.selectWishlistByIndex(index);
        if (dbWishlist == null || dbWishlist.isDeleted()) { //위시리스트에 없거나 삭제됨
            return PurchaseResult.FAILURE_NOT_FOUND;
        }
        // 장바구니에 존재하는 지 중복 체크
        if(DuplicationCheckCart(userEmail, gameIndex)) {
            return PurchaseResult.FAILURE_DUPLICATE_CART;
        }
        // 장바구니에 없다면 CartEntity 생성해서 값 셋팅
        CartEntity cart = new CartEntity(user.getEmail(), gameIndex, LocalDateTime.now());
        // db에 삽입
        if(this.purchaseMapper.insertCart(cart) <= 0) {
            throw new TransactionalException("오류: 장바구니 삽입 실패");
        }
        return CommonResult.SUCCESS;
    }

    /** 위시리스트 제거 */
    @Transactional
    public Result deleteFromWishlist(int index) {
        if (index < 1) {
            return CommonResult.FAILURE;
        }
        WishlistEntity dbWishlist = this.purchaseMapper.selectWishlistByIndex(index);
        if (dbWishlist == null || dbWishlist.isDeleted()) { //위시리스트에 없거나 삭제됨
            return PurchaseResult.FAILURE_NOT_FOUND;
        }

        dbWishlist.setDeleted(true); // 삭제됨
        if (this.purchaseMapper.updateWishlistByIndex(dbWishlist) <= 0){
            throw new TransactionalException("오류: 위시리스트 삭제 실패");
        }
        return CommonResult.SUCCESS;
    }

    /** 위시리스트 제거 취소 시 */
    public Result updateIsDeleteWishlist(int index) {
        if (index < 1) {
            return CommonResult.FAILURE;
        }
        System.out.println(index);
        // 위시리스트 가져옴
        WishlistEntity dbWishlist = this.purchaseMapper.selectAllWishlistByIndex(index);
        //위시리스트에 없음
        if (dbWishlist == null) {
            return PurchaseResult.FAILURE_NOT_FOUND;
        }
        // 위시리스트에는 있는데, 제거되지않았음.
        if (dbWishlist.isDeleted()) {
            return PurchaseResult.FAILURE_DUPLICATE_WISHLIST;
        }
        dbWishlist.setDeleted(false); // 삭제 취소(isDelete를 false로)
        if (this.purchaseMapper.updateWishlistByIndex(dbWishlist) <= 0){
            throw new TransactionalException("오류: 위시리스트 삭제 실패");
        }
        return CommonResult.SUCCESS;
    }
    //endregion

    /** 실제로 유저와 게임이 장바구니에 있는 지(중복인지) 체크 */
    private boolean DuplicationCheckCart(String userEmail, int gameIndex) {
        // 장바구니에 있는 지 확인
        CartEntity dbCart = this.purchaseMapper.selectCartByEmailANDGameIndex(userEmail, gameIndex);
        return dbCart != null; // 장바구니가 있음 true 반환
    }

    /** 실제로 유저와 게임이 장바구니에 있는 지(중복인지) 체크 */
    private boolean DuplicationCheckWishlist(String userEmail, int gameIndex) {
        // 위시리스트에 있는 지
        WishlistEntity dbWishlist = this.purchaseMapper.selectWishlistByEmailANDGameIndex(userEmail, gameIndex);

        if (dbWishlist == null || dbWishlist.isDeleted()) {
            return false;
        }else {
            return true;// 위시리스트에 있음
        }
    }


    //endregion
    //region 결제 관련
    /** user 객체를 받아와서 장바구니에 있는 게임들 구매(장바구니 삭제, 구매 내역 추가) */
    @Transactional
    public Result proceedToCheckout(UserEntity user) {
        if (user == null || user.getEmail() == null) {
            return CommonResult.FAILURE;
        }
        // 장바구니에 관련된 정보들을 가져옴
        CartDTO[] cartList = getCarts(user);
        if (cartList == null || cartList.length == 0) {
            return PurchaseResult.FAILURE_NOT_FOUND;
        }
        // 구매 내역 삽입과 장바구니 삭제
        for (CartDTO cart: cartList) {
            // 구매 내역에 삽입할 내용 설정 (CartDTO 이용)
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setUserEmail(user.getEmail());//유저 이메일
            purchase.setGameIndex(cart.getGame().getIndex());// 구매하는 게임
            purchase.setDate(LocalDateTime.now()); //구매일 현제 날짜로 설정
            purchase.setGamePrice(cart.getGame().getPrice());//게임 가격(오리지널 가격)
            purchase.setFinalPrice(cart.getPrice().getCurrentPrice());//할인 적용된 가격
            purchase.setDiscountAmount(cart.getPrice().getDiscountPrice()); //할인 가격
            purchase.setAddr(user.getAddr()); // 유저 주소
            purchase.setDeletedAt(null); // 삭제일 기본으로 null

            // 구매 내역 삽입
            if (this.purchaseMapper.insertPurchase(purchase) <= 0){
                throw new TransactionalException("오류: 구매 내역 삽입 실패");
            }

            // 장바구니 제거
            if (this.purchaseMapper.deleteCartByIndex(cart.getCart().getIndex()) <= 0) {
                throw new TransactionalException("오류: 장바구니 삭제 실패");
            }

            // 위시리스트도 제거(존재하면 제거)
            WishlistEntity wishlist = this.purchaseMapper.selectWishlistByEmailANDGameIndex(user.getEmail(), cart.getGame().getIndex()); //삭제할 위시리스트 설정
            // 위시리스트가 있으면 제거
            if (wishlist != null) {
                wishlist.setDeleted(true);
                if (this.purchaseMapper.updateWishlistByIndex(wishlist) <= 0){
                    throw new TransactionalException("오류: 위시리스트 삭제 실패");
                }
            }
        }
        return CommonResult.SUCCESS;
    }
    //endregion

}