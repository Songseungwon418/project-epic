package com.ssw.epicgames.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.PayEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.PurchaseService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @Value("${kakaopay.appkey}")
    private String kakaopayAppKey;

    @Value("${imp.number}")
    private String impNumber;

    //region 장바구니 관련
    /** 장바구니 화면 출력 */
    @GetMapping(value = "/cart", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCart(@SessionAttribute(value = "user", required = false) UserEntity user) {
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
        else { // 장바구니에서 위시리스트 이동 시
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
    public ModelAndView getPay(@SessionAttribute(value = "user", required = false) UserEntity user) {
        CartDTO[] carts = this.purchaseService.getCarts(user);
        ModelAndView mav = new ModelAndView();
        if (carts != null) {
            mav.addObject("user", user);
            mav.addObject("carts", carts);
            // 카카오페이 결재를 위한 AppKey 넘겨줌
            mav.addObject("kakaoAppKey", kakaopayAppKey);
            mav.addObject("impNumber", impNumber);
        }
        mav.setViewName("purchase/pay");
        return mav;
    }

    /** 결제 진행 - 주문하기 버튼을 눌러서 결제 완료 시 */
    @PostMapping(value = "/pay/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String confirmPay(
            @SessionAttribute(value = "user", required = true) UserEntity user,
            @RequestParam(value = "userEmail", required = true) String userEmail,
            @RequestParam(value = "pay", required = true) String payJSON
    ) throws JsonProcessingException {
        // payJson을 객체로 변환 (Jackson 라이브러리 사용)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 날짜/시간 모듈 등록
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // ISO-8601 형식으로 날짜 출력
        PayEntity pay = objectMapper.readValue(payJSON, PayEntity.class);

        Result result;

        // 로그인이 안되었거나 로그인 한 유저와 구매 요청한 유저가 다를 때
        if (user == null || !userEmail.equals(user.getEmail())) {
            result = CommonResult.FAILURE;
        }
        // 유저가 동일하다면 구매 진행
        else {
            result = this.purchaseService.proceedToCheckout(user, pay);
        }
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    /** 결재 완료(주문 성공 시) 화면 출력 */
    @GetMapping(value = "/paysuccess", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPaysuccess() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("purchase/paysuccess");
        return mav;
    }
    //endregion



}
