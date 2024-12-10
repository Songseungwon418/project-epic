package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.CartEntity;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GameRatingEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.GameService;
import com.ssw.epicgames.services.PurchaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final GameService gameService;

    public PurchaseController(PurchaseService purchaseService, GameService gameService) {
        this.purchaseService = purchaseService;
        this.gameService = gameService;
    }

    //region 장바구니 관련
    @GetMapping(value = "/cart", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCart(
//            @SessionAttribute(value = "user", required = false) UserEntity user
    ) {
        String user = "ttig0614@naver.com";
        CartDTO[] carts = this.purchaseService.getCarts(user);
        ModelAndView mav = new ModelAndView();
        if (carts != null) {
            mav.addObject("carts", carts);
        }
        mav.setViewName("purchase/cart");
        return mav;
    }
    //endregion

    //region 위시리스트 관련
    @GetMapping(value = "/wishlist", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWishlist(
//            @SessionAttribute(value = "user", required = false) UserEntity user
    ) {
        String user = "ttig0614@naver.com";
        WishlistDTO[] wishlists =this.purchaseService.getWishlists(user);
        ModelAndView mav = new ModelAndView();
        if (wishlists != null) {
            mav.addObject("wishlists", wishlists);
        }
        mav.setViewName("purchase/wishlist");
        return mav;
    }
    //endregion

    //region 결제 관련
    @GetMapping(value = "/pay", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPay() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("purchase/pay");
        return mav;
    }
    //endregion



}
