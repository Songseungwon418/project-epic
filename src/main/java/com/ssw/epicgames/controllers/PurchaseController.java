package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.CartEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.entities.WishlistEntity;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.PurchaseService;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    //region 장바구니 관련
    /** 장바구니 화면 출력 */
    @GetMapping(value = "/cart", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCart(
            @SessionAttribute(value = "user", required = false) UserEntity user
    ) {
        System.out.println(user);
        CartDTO[] carts = this.purchaseService.getCarts(user);
        ModelAndView mav = new ModelAndView();
        if (carts != null) {
            mav.addObject("carts", carts);
        }
        mav.setViewName("purchase/cart");
        return mav;
    }

    /** 장바구니 담기 */
    @PostMapping(value = "/cart/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addToCart(@RequestParam(value = "cart", required = false) CartEntity cart){
        Result result = this.purchaseService.addToCart(cart);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    /** 장바구니 제거 */
    @DeleteMapping(value = "/cart/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteToCart(
            @SessionAttribute(value = "user", required = false) UserEntity user,
            @RequestParam(value = "gameIndex", required = false) int gameIndex,
            @RequestParam(value = "index", required = false) int index
    ){
        Result result = this.purchaseService.deleteFromCart(user, gameIndex, index);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
    //endregion

    //region 위시리스트 관련
    /** 위시리스트 화면 출력 */
    @GetMapping(value = "/wishlist", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWishlist(
            @SessionAttribute(value = "user", required = false) UserEntity user
    ) {
        WishlistDTO[] wishlists =this.purchaseService.getWishlists(user);
        ModelAndView mav = new ModelAndView();
        if (wishlists != null) {
            mav.addObject("wishlists", wishlists);
        }
        mav.setViewName("purchase/wishlist");
        return mav;
    }

    /** 위시리스트 담기 */
    @PostMapping(value = "/wishlist/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addToWishlist(@RequestParam(value = "wishlist", required = false) WishlistEntity wishlist){
        Result result = this.purchaseService.addToWishlist(wishlist);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    /** 위시리스트 제거 */
    @PatchMapping(value = "/wishlist/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteToWishlist(@RequestParam(value = "wishlist", required = false) WishlistEntity wishlist){
        Result result = this.purchaseService.deleteFromWishlist(wishlist);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    //endregion

    //region 결제 관련
    /** 결재 화면 출력 */
    @GetMapping(value = "/pay", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPay() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("purchase/pay");
        return mav;
    }
    //endregion



}
