package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.PayDTO;
import com.ssw.epicgames.DTO.PurchaseDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.exceptions.TransactionalException;
import com.ssw.epicgames.mappers.GameRatingMapper;
import com.ssw.epicgames.mappers.PurchaseMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.resutls.purchase.PurchaseResult;
import com.ssw.epicgames.vos.PriceVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


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

        // 이미 구매한 게임인지 확인
        if (checkDuplicatePurchaseByGameIndex(user.getEmail(), cart.getGameIndex())) {
            return PurchaseResult.FAILURE_DUPLICATE_PURCHASE;
        }

        // 장바구니에 넣을 게임의 등급 검색
        String gameGrac = gameService.getGameByIndex(cart.getGameIndex()).getGrGrac();

        // 유저의 나이가 게임의 등급 이상인지 - 게임등급 이상이 아니면
        if(!isEligibleForGame(user.getBirthdate(), gameGrac)){
            return PurchaseResult.FAILURE_AGE_LIMIT;
        }

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

        // 이미 구매한 게임인지 확인
        if (checkDuplicatePurchaseByGameIndex(user.getEmail(), wishlist.getGameIndex())) {
            return PurchaseResult.FAILURE_DUPLICATE_PURCHASE;
        }

        // 위시리스트 db에 삽입
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

        // 장바구니에 넣을 게임의 등급 검색
        String gameGrac = gameService.getGameByIndex(cart.getGameIndex()).getGrGrac();

        // 유저의 나이가 게임의 등급 이상인지 - 게임등급 이상이 아니면
        if(!isEligibleForGame(user.getBirthdate(), gameGrac)){
            return PurchaseResult.FAILURE_AGE_LIMIT;
        }

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

        // 해당 위시리스트가 있는지 여부
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

//region 결제 관련
    /** 결제 내역, 구매 내역 추가 및 장바구니, 위시리스트 삭제 */
    @Transactional
    public Result proceedToCheckout(UserEntity user, PayEntity pay, CartDTO[] carts) {
        if (user == null || user.getEmail() == null || pay == null || carts == null || carts.length == 0) {
            return CommonResult.FAILURE;
        }

        // 이미 구매한 게임인지 확인
        if (checkDuplicatePurchase(user.getEmail(), carts)) {
            return PurchaseResult.FAILURE_DUPLICATE_PURCHASE;
        }


        // 결제 내역 삽입
        if(this.purchaseMapper.insertPay(pay) <= 0){
            throw new TransactionalException("오류: 결제 내역 삽입 실패");
        }

        // 구매 내역 삽입과 장바구니 삭제
        for (CartDTO cart: carts) {

            // 유저의 나이가 게임의 등급 이상인지 - 게임등급 이상이 아니면
            if(!isEligibleForGame(user.getBirthdate(), cart.getGame().getGrGrac())){
                return PurchaseResult.FAILURE_AGE_LIMIT;
            }

            // 장바구니와 일치하면 구매 내역 생성 및 삽입, 장바구니 제거, 위시리스트 제거
            if (DuplicationCheckCart(user.getEmail(), cart.getGame().getIndex())) {
                // 구매 내역 생성
                PurchaseEntity purchase = creatPurchaseEntity(user.getEmail(), user.getAddr(), pay.getId(), cart.getGame().getIndex(), cart.getPrice());

                // 구매 내역 삽입
                if (this.purchaseMapper.insertPurchase(purchase) <= 0){
                    throw new TransactionalException("오류: 구매 내역 삽입 실패");
                }

                // 장바구니 제거
                if (deleteFromCart(cart.getCart().getIndex()) != CommonResult.SUCCESS) {
                    throw new TransactionalException("오류: 장바구니 삭제 실패");
                }

                // 위시리스트도 제거(존재하면 제거)
                WishlistEntity wishlist = this.purchaseMapper.selectWishlistByEmailANDGameIndex(user.getEmail(), cart.getGame().getIndex()); //삭제할 위시리스트 설정
                // 위시리스트가 있으면 제거
                if (wishlist != null) {
                    if(deleteFromWishlist(wishlist.getIndex()) != CommonResult.SUCCESS) {
                        throw new TransactionalException("오류: 위시리스트 삭제 실패");
                    }
                }
            }else {
                return PurchaseResult.FAILURE_NOT_FOUND; //결제 요청한 목록이 장바구니에 있는 목록과 다름(이상한 요청일때)
            }
        }
        return CommonResult.SUCCESS;
    }

    /** 게임 단품 구매일 경우 */
    @Transactional
    public Result buyGame(UserEntity user, PayEntity pay, int gameIndex) {
        if (user == null || user.getEmail() == null || pay == null || gameIndex == 0) {
            return CommonResult.FAILURE;
        }

        // db에서 게임 확인
        GameEntity buyGame = this.gameService.getGameByIndex(gameIndex);

        // 이미 구매한 게임인지 확인
        if (checkDuplicatePurchaseByGameIndex(user.getEmail(), buyGame.getIndex())) {
            return PurchaseResult.FAILURE_DUPLICATE_PURCHASE;
        }

        // 유저의 나이가 게임의 등급 이상인지 - 게임등급 이상이 아니면
        if(!isEligibleForGame(user.getBirthdate(), buyGame.getGrGrac())){
            return PurchaseResult.FAILURE_AGE_LIMIT;
        }

        // 결제 내역 삽입
        if(this.purchaseMapper.insertPay(pay) <= 0){
            throw new TransactionalException("오류: 결제 내역 삽입 실패");
        }

        // 게임의 할인 정보도 가져옴
        PriceVo price = this.priceService.discountInfo(buyGame.getIndex(), buyGame.getPrice());

        // 구매 내역 생성
        PurchaseEntity purchase = creatPurchaseEntity(user.getEmail(), user.getAddr(), pay.getId(), gameIndex, price);

        // 구매 내역 삽입
        if (this.purchaseMapper.insertPurchase(purchase) <= 0){
            throw new TransactionalException("오류: 구매 내역 삽입 실패");
        }

        // 장바구니에 담겨져 있으면 제거
        CartEntity cart = this.purchaseMapper.selectCartByEmailANDGameIndex(user.getEmail(), buyGame.getIndex());
        if (cart != null) {
            if (deleteFromCart(cart.getIndex()) != CommonResult.SUCCESS) {
                throw new TransactionalException("오류: 장바구니 삭제 실패");
            }
        }

        // 위시리스트에 담겨져있으면 제거
        WishlistEntity wishlist = this.purchaseMapper.selectWishlistByEmailANDGameIndex(user.getEmail(), buyGame.getIndex()); //삭제할 위시리스트 설정
        // 위시리스트가 있으면 제거
        if (wishlist != null) {
            if(deleteFromWishlist(wishlist.getIndex()) != CommonResult.SUCCESS) {
                throw new TransactionalException("오류: 위시리스트 삭제 실패");
            }
        }
        return CommonResult.SUCCESS;
    }

    /** 결제 내역 조회(단일) */
    public PayEntity getPayById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return this.purchaseMapper.selectPayById(id);
    }

    /** 결제 및 구매 내역들 조회 */
    public List<PayDTO> getPurchasesByUser(UserEntity user) {
        // 유저 유효성 검사(로그인 유뮤)
        if (user == null) {
            return null;
        }

        List<PayDTO> payDTOList = new ArrayList<>();  // 결제 내역 담을 PayDTO 객체를 저장할 리스트 -> 결제 내역 목록들

        // 유저의 결제 내역들
        List<PayEntity> payList = this.purchaseMapper.selectPayByUser(user.getEmail());
        for (PayEntity pay: payList) {
            int totalGameAmount = 0;
            int totalDiscount = 0;
            String payId = pay.getId();
            List<PurchaseEntity> purchaseList = this.purchaseMapper.selectPurchaseByPayId(payId);
            List<PurchaseDTO> purchaseDTOList = new ArrayList<>();  // PurchaseDTO 객체를 저장할 PurchaseDTO 리스트
            for (PurchaseEntity purchase: purchaseList) {
                GameEntity game = this.gameService.getGameByIndex(purchase.getGameIndex());
                PriceVo price = this.priceService.discountInfo(game.getIndex(), game.getPrice());

                // 환불 유무 확인(구매면 null)
                if (purchase.getDeletedAt() == null){
                    totalGameAmount += game.getPrice();//원본 게임 가격 더함(총 가격 구하기)
                    totalDiscount += price.getDiscountPrice(); // 할인가격 더함(총 할인 가격 구하기)
                }

                // PurchaseDTO 객체 생성
                PurchaseDTO purchaseDTO = PurchaseDTO.builder()
                        .purchase(purchase)
                        .game(game)
                        .price(price)
                        .build();

                // PurchaseDTO 리스트에 추가
                purchaseDTOList.add(purchaseDTO);
            }

            boolean isRefund = checkIsRefund(pay.getPaidAt());

            // PayDTO 객체 생성
            PayDTO payDTO = PayDTO.builder()
                    .pay(pay)
                    .purchase(purchaseDTOList.isEmpty() ? null : purchaseDTOList)
                    .totalGameAmount(totalGameAmount) // 할인적용안된 게임의 총 가격
                    .totalDiscount(totalDiscount) // 총 할인금액
                    .isRefund(isRefund) //환불여부
                    .build();

            // 최종 PayDTO 리스트에 추가
            payDTOList.add(payDTO);
        }
        return payDTOList;
    }

    /** 결제 내역 및 구매 내역 수정(전체 환불 시) */
    @Transactional
    public Result updateProceed(UserEntity user, String payId) {
        // 데이터 유효성 검사
        if (user == null) { // 유저 유효성 검사(로그인 유뮤)
            return CommonResult.FAILURE_UNSIGNED;
        }
        // 요청한 결제 내역 아이디 유효성 검사
        else if (payId == null || payId.isEmpty()) {
            return CommonResult.FAILURE;
        }

        //결제 내역 조회 및 해당하는 구매내역들 조회
        PayEntity pay = this.purchaseMapper.selectPayById(payId);
        List<PurchaseEntity> purchaseList = this.purchaseMapper.selectPurchaseByPayId(payId);

        // 찾을 수 없는 결제 내역 - 잘못된 요청임
        if (pay == null) {
            return CommonResult.FAILURE;
        }
        // 현재 로그인한 유저와 요청한 결제 내역의 유저가 일치한 지 확인
        else if (!pay.getUserEmail().equals(user.getEmail())) {
            return CommonResult.FAILURE;
        }
        // 이미 전체 환불된 결제 내역임
        else if (pay.getRefundAmount() == pay.getAmount()) {
            return PurchaseResult.FAILURE_DUPLICATE_REFUND; //중복 환불
        }

        //결제 내역에서 환불 기간 내 환불인지 확인
        boolean isRefund = checkIsRefund(pay.getPaidAt());
        if (!isRefund) {
            return PurchaseResult.FAILURE_DATE_PASSED;
        }

        // 부분 환불일 경우 이미 환불된 금액을 제외한 나머지 금액 더해줌
        if (pay.getStatus().equals("partial")){
            pay.setRefundAmount(pay.getRefundAmount() + (pay.getAmount()-pay.getRefundAmount()) );
        }else {
            pay.setRefundAmount(pay.getAmount());
        }
        // 조회한 결제 내역의 환불금액을 결제금액값으로 수정, 상태 cancelled, 업데이트 날짜를 현재날짜로 수정
        pay.setStatus("cancelled");
        pay.setUpdatedAt(LocalDateTime.now());

        if (this.purchaseMapper.updatePay(pay) <= 0) {
            throw new TransactionalException("오류: 전체 환불 실패");
        }

        // 구매 내역들의 deleted_at을 현재날짜로
        for (PurchaseEntity purchase: purchaseList) {
            if (purchase.getDeletedAt() == null){
                purchase.setDeletedAt(LocalDateTime.now());
                if (this.purchaseMapper.updatePurchase(purchase) <= 0) {
                    throw new TransactionalException("오류: 전체 환불 실패");
                }
            }
        }
        return CommonResult.SUCCESS;
    }

    /** 결제 내역 및 구매 내역 수정(부분 환불 시) */
    @Transactional
    public Result updateProceed(UserEntity user, String payId, int gameIndex) {
        // 데이터 유효성 검사
        if (user == null) { // 유저 유효성 검사(로그인 유뮤)
            return CommonResult.FAILURE_UNSIGNED;
        }
        // 요청한 결제 내역 아이디 유효성 검사
        else if (payId == null || payId.isEmpty() || gameIndex <= 0) {
            return CommonResult.FAILURE;
        }

        // payId로 결제 내역 조회
        PayEntity pay = this.purchaseMapper.selectPayById(payId);
        // 찾을 수 없는 결제 내역 - 잘못된 요청임
        if (pay == null) {
            return CommonResult.FAILURE;
        }
        // 현재 로그인한 유저와 요청한 결제 내역의 유저가 일치한 지 확인
        else if (!pay.getUserEmail().equals(user.getEmail())) {
            return CommonResult.FAILURE;
        }
        // 전체 환불이 되었는 지 확인
        else if (pay.getStatus().equals("cancelled") && pay.getRefundAmount() == pay.getAmount()) {
            return PurchaseResult.FAILURE_DUPLICATE_REFUND; //중복 환불
        }

        // 조회된 결제내역의 payId와 gameIndex에 해당하는 구매내역
        PurchaseEntity purchase = this.purchaseMapper.selectPurchaseByPayIdANDGameIndex(pay.getId(), gameIndex);
        // 이미 환불된 구매 내역인지 확인
        if (purchase.getDeletedAt() != null) {
            return PurchaseResult.FAILURE_DUPLICATE_REFUND; //중복 환불
        }
        // 부분환불이 된 경우 환불금액이 환불할 수 있는 금액보다 클 경우 환불 불가
        else if (pay.getStatus().equals("partial")) {
            if (purchase.getFinalPrice() > (pay.getAmount() - pay.getRefundAmount())){
                return CommonResult.FAILURE;
            }
        }else {
            // 상태 partial
            pay.setStatus("partial");
        }

        //업데이트 날짜를 현재날짜로 수정
        pay.setUpdatedAt(LocalDateTime.now());
        // 조회한 결제 내역의 환불 금액에 환불할 금액을 더해줌
        pay.setRefundAmount(pay.getRefundAmount() + purchase.getFinalPrice());

        if (pay.getRefundAmount() == pay.getAmount()) {
            // 상태 partial
            pay.setStatus("cancelled");
        }

        // 결제내역 db 수정
        if (this.purchaseMapper.updatePay(pay) <= 0) {
            throw new TransactionalException("오류: 환불 실패");
        }

        // 해당 구매내역 삭제일을 현재날짜로 수정
        purchase.setDeletedAt(LocalDateTime.now());
        // 구매내역 db 수정
        if (this.purchaseMapper.updatePurchase(purchase) <= 0) {
            throw new TransactionalException("오류: 환불 실패");
        }

        return CommonResult.SUCCESS;
    }

//endregion

//region 장바구니, 위시리스트 중복 확인하는 메서드
    /** 실제로 유저와 게임이 장바구니에 있는 지(중복인지) 체크 */
    public boolean DuplicationCheckCart(String userEmail, int gameIndex) {
        // 장바구니에 있는 지 확인
        CartEntity dbCart = this.purchaseMapper.selectCartByEmailANDGameIndex(userEmail, gameIndex);
        return dbCart != null; // 장바구니가 있음 true 반환
    }

    /** 실제로 유저와 게임이 위시리스트에 있는 지(중복인지) 체크 */
    public boolean DuplicationCheckWishlist(String userEmail, int gameIndex) {

        // 위시리스트에 있는 지
        WishlistEntity dbWishlist = this.purchaseMapper.selectWishlistByEmailANDGameIndex(userEmail, gameIndex);

        // 위시리스트에 있음
        return dbWishlist != null && !dbWishlist.isDeleted();
    }
//endregion

//region 이미 구매한 게임이 있는지 확인하는 메서드
    /** 장바구니에 담긴 목록들에서 이미 구매한 게임이 있는지 체크 */
    public boolean checkDuplicatePurchase(String userEmail, CartDTO[] cartList) {
        for (CartDTO cart : cartList) {
            if (checkDuplicatePurchaseByGameIndex(userEmail, cart.getGame().getIndex())) {
                return true; // 중복 구매가 있으면 true 리턴
            }
        }
        return false; // 중복 구매가 없으면 false 리턴
    }

    /** 구매할 게임이 이미 구매한 것인지 체크 */
    public boolean checkDuplicatePurchaseByGameIndex(String userEmail, int gameIndex) {
        return this.purchaseMapper.selectPurchaseByGameIndex(userEmail, gameIndex) > 0; // 중복 구매가 있으면 true 리턴
    }
//endregion

//region 결제 내역 객체 생성 메서드
    private PurchaseEntity creatPurchaseEntity(String userEmail, String userAddr, String payID, int gameIndex, PriceVo price) {
        // 구매 내역에 삽입할 내용 설정 (CartDTO 이용)
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setUserEmail(userEmail);//유저 이메일
        purchase.setPayId(payID); //삽입한 결제 내역 id
        purchase.setGameIndex(gameIndex);// 구매하는 게임
        purchase.setDate(LocalDateTime.now()); //구매일 현제 날짜로 설정
        purchase.setGamePrice(price.getOriginalPrice());//게임 가격(오리지널 가격)
        purchase.setFinalPrice(price.getCurrentPrice());//할인 적용된 가격
        purchase.setDiscountAmount(price.getDiscountPrice()); //할인 가격
        purchase.setAddr(userAddr); // 유저 주소
        purchase.setDeletedAt(null); // 삭제일 기본으로 null

        return purchase;
    }
//endregion

//region 환불 가능 여부 확인하는 메서드
    /** 72시간 이내인지 확인
     * @param paidAt: LocalDateTime (구매일 입력)
     * */
    public boolean checkIsRefund(LocalDateTime paidAt){
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간 기준으로 72시간 이내인지 확인 - 환불여부
        return ChronoUnit.HOURS.between(paidAt, now) <= 72;
    }
//endregion

//region 게임 등급을 체크하는 메서드
public static boolean isEligibleForGame(LocalDate birthDate, String gameRating) {
    LocalDate currentDate = LocalDate.now();

    // 나이 계산
    Period agePeriod = Period.between(birthDate, currentDate);
    int age = agePeriod.getYears();

    // 게임 등급에 맞는 조건 체크
    return switch (gameRating) {
        case "ALL" -> true; // 전체 이용 가능: 나이와 관계없이 모두 가능
        case "12Y" -> age >= 12; // 12세 이상 이용 가능
        case "15Y" -> age >= 15; // 15세 이상 이용 가능
        case "ADULT" -> age >= 19; // 19세 이상 이용 가능
        default -> false;
    };
}

//endregion
}
