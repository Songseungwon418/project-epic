package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.PurchaseService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

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
            @RequestParam(value = "index", required = false) int index,
            @RequestParam(value = "userEmail", required = false) String userEmail
    ){
        Result result;
        if (index <= 0) {
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
            @RequestParam(value = "index", required = false) int index,
            @RequestParam(value = "userEmail", required = false) String userEmail
    ) {
        Result result;
        if (index <= 0) {
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
        }
        mav.setViewName("purchase/pay");
        return mav;
    }

    @PostMapping(value = "/pay/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String confirmPay(
            @SessionAttribute(value = "user", required = true) UserEntity user,
            @RequestParam(value = "userEmail", required = true) String userEmail
    ){
        Result result;
        // 로그인이 안되었거나 로그인 한 유저와 구매 요청한 유저가 다를 때
        if (user == null || !userEmail.equals(user.getEmail())) {
            result = CommonResult.FAILURE;
        }
        // 유저가 동일하다면 구매 진행
        else {
            result = this.purchaseService.proceedToCheckout(user);
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
