package com.ssw.epicgames.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.PayDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.PayEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.resutls.purchase.PurchaseResult;
import com.ssw.epicgames.services.GameService;
import com.ssw.epicgames.services.PriceService;
import com.ssw.epicgames.services.PurchaseService;
import com.ssw.epicgames.vos.PriceVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final GameService gameService;
    private final PriceService priceService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService, GameService gameService, PriceService priceService) {
        this.purchaseService = purchaseService;
        this.gameService = gameService;
        this.priceService = priceService;
    }

    @Value("${kakaopay.appkey}")
    private String kakaopayAppKey;

    @Value("${imp.number}")
    private String impNumber;


    //region 장바구니 관련
    /** 장바구니 화면 출력 */
    @GetMapping(value = "/cart", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCart(@SessionAttribute(value = "user", required = false) UserEntity user) {
//        if (user == null) {
//            return new ModelAndView("redirect:/user/");
//        }
        CartDTO[] carts = this.purchaseService.getCarts(user);
        ModelAndView mav = new ModelAndView();
        if (carts != null) {
            mav.addObject("user", user);
            mav.addObject("carts", carts);
        }
        mav.setViewName("purchase/cart");
        return mav;
    }

    /** 장바구니 담기 */
    @PostMapping(value = "/cart/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addToCart(
            @SessionAttribute(value = "user", required = true) UserEntity user,
            @RequestParam(value = "gameIndex", required = true) int gameIndex,
            @RequestParam(value = "index", required = false, defaultValue = "0") int index,
            @RequestParam(value = "userEmail", required = false) String userEmail
    ){
        Result result;
        if (index == 0) {
            result = this.purchaseService.addToCart(user, gameIndex);
        }
        else { // 위시리스트에서 장바구니 담기
            result = this.purchaseService.addToCart(user, gameIndex, index, userEmail);
        }
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    /** 장바구니 제거 */
    @DeleteMapping(value = "/cart/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteToCart(@RequestParam(value = "index", required = true) int index){
        Result result = this.purchaseService.deleteFromCart(index);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
    //endregion

    //region 위시리스트 관련
    /** 위시리스트 화면 출력 */
    @GetMapping(value = "/wishlist", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWishlist(@SessionAttribute(value = "user", required = false) UserEntity user) {
//        if (user == null) {
//            return new ModelAndView("redirect:/user/");
//        }
        WishlistDTO[] wishlists =this.purchaseService.getWishlists(user);
        ModelAndView mav = new ModelAndView();
        if (wishlists != null) {
            mav.addObject("user", user);
            mav.addObject("wishlists", wishlists);
        }
        mav.setViewName("purchase/wishlist");
        return mav;
    }

    /** 위시리스트 담기 */
    @PostMapping(value = "/wishlist/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addToWishlist(
            @SessionAttribute(value = "user", required = true) UserEntity user,
            @RequestParam(value = "gameIndex", required = true) int gameIndex,
            @RequestParam(value = "index", required = false, defaultValue = "0") int index,
            @RequestParam(value = "userEmail", required = false) String userEmail
    ) {
        Result result;
        if (index == 0) {
            result = this.purchaseService.addToWishlist(user, gameIndex);
        }
        else {
            result = this.purchaseService.addToWishlist(user, gameIndex, index, userEmail);
        }
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    /** 위시리스트 제거 */
    @PatchMapping(value = "/wishlist/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteToWishlist(@RequestParam(value = "index", required = true) int index){
        Result result = this.purchaseService.deleteFromWishlist(index);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    /** 위시리스트 제거 취소 */
    @PatchMapping(value = "/wishlist/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String cancelDeleteWishlist(@RequestParam(value = "index", required = true) int index){
        Result result = this.purchaseService.updateIsDeleteWishlist(index);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
    //endregion

    //region 결제 관련
    /** 결재 화면 출력 */
    @GetMapping(value = "/pay", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPay(
            @SessionAttribute(value = "user", required = false) UserEntity user,
            @RequestParam(value = "index", required = false, defaultValue = "0") int gameIndex
    ) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("user", user);

        // 카카오페이 결재를 위한 AppKey 넘겨줌
        mav.addObject("kakaoAppKey", kakaopayAppKey);
        mav.addObject("impNumber", impNumber);

        // 로그인 유무, 로그인이 안되었을 시 로그인 화면으로
//        if (user == null) {
//            return new ModelAndView("redirect:/user/");
//        }
        //장바구니 페이지에서 구매 버튼을 누를 시
        if (gameIndex == 0) {
            // db에서 장바구니에 담긴 목록들 불러옴
            CartDTO[] carts = this.purchaseService.getCarts(user);
            // 장바구니가 비여있지않으면 목록을 보여줌
            if (carts != null) {
                CartDTO lastCart = carts[carts.length - 1];
                mav.addObject("totalPrice", lastCart.getTotalPrice());
                mav.addObject("carts", carts);
                mav.setViewName("purchase/pay");
            }
        }
        // 게임 상세페이지에서 바로 구매 버튼을 누를 시
        else if (gameIndex > 0) {
            // db에서 해당하는 게임의 정보가 할인 정보 가지고옴
            GameEntity game = this.gameService.getGameByIndex(gameIndex);
            PriceVo price = this.priceService.discountInfo(game.getIndex(), game.getPrice());
            // 유저와 게임, 할인 정보를 넘겨줌
            mav.addObject("game", game);
            mav.addObject("price", price);
            mav.addObject("totalPrice", price.getCurrentPrice());
            mav.setViewName("purchase/pay");
        }
        // 잘못된 요청일 경우
        else {
            return new ModelAndView("redirect:/");
        }
        return mav;
    }


    /** 결제 진행 - 주문하기 버튼을 눌러서 결제 완료 시 */
    @PostMapping(value = "/pay/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String confirmPay(
            @SessionAttribute(value = "user", required = true) UserEntity user,
            @RequestParam(value = "userEmail", required = false) String userEmail,
            @RequestParam(value = "pay", required = true) String payJSON,
            @RequestParam(value = "gameIndex", required = false, defaultValue = "0") int gameIndex
    ) throws JsonProcessingException {
        // payJson을 객체로 변환 (Jackson 라이브러리 사용)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 날짜/시간 모듈 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // ISO-8601 형식으로 날짜 출력
        PayEntity pay = objectMapper.readValue(payJSON, PayEntity.class);

        Result result; //결과 저장

        // 로그인이 안되었거나 로그인 한 유저와 구매 요청한 유저가 다를 때
        if (user == null || !userEmail.equals(user.getEmail())) {
            result = CommonResult.FAILURE;
        }
        // 게임상세페이지에서 구매가 아닐 시
        else if(gameIndex == 0){
            CartDTO[] carts = this.purchaseService.getCarts(user);
            // 장바구니에서 구매 시
            if (carts == null || carts.length == 0) { // 장바구니에 구매할 게임이 없음
                result = PurchaseResult.FAILURE_NOT_FOUND;
            }else {
                result = this.purchaseService.proceedToCheckout(user, pay, carts);
            }
        }
        // 장바구니 구매가 아니고 게임 상세페이지에서 게임 하나 구매 시
        else if (gameIndex > 0){
            result = this.purchaseService.buyGame(user, pay, gameIndex);
        }
        else { // 구매할 게임이 없는 요청임
            result = PurchaseResult.FAILURE_NOT_FOUND;
        }

        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    /** 결재 완료(주문 성공 시) 화면 출력 */
    @GetMapping(value = "/paysuccess", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPaysuccess(@RequestParam(value = "id", required = true) String id) {
        ModelAndView mav = new ModelAndView();
        PayEntity pay = this.purchaseService.getPayById(id);
        mav.addObject("pay", pay);
        mav.setViewName("purchase/paysuccess");
        return mav;
    }

    /** 환불 요청 시 */
    @PatchMapping(value = "/patch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchPay(@SessionAttribute(value = "user", required = true) UserEntity user,
                           @RequestParam(value = "payId", required = true) String payId,
                           @RequestParam(value = "gameIndex", required = false, defaultValue = "0") int gameIndex){
        Result result; //결과 저장
        // 로그인 유무
        if (user == null) {
            result = CommonResult.FAILURE;
        } else if (gameIndex == 0) { // 전체 환불
            result = this.purchaseService.updateProceed(user, payId);
        } else if (gameIndex > 0) { // 부분 환불
            result = this.purchaseService.updateProceed(user, payId, gameIndex);
        } else { // 그 외 알수없는 요청
            result = CommonResult.FAILURE;
        }
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
    //endregion
}
