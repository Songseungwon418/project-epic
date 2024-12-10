package com.ssw.epicgames.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController {

    //region 장바구니 관련
    @GetMapping(value = "/cart", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCart() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("purchase/cart");
        return mav;
    }

    //endregion

    //region 위시리스트 관련
    @GetMapping(value = "/wishlist", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWishlis() {
        ModelAndView mav = new ModelAndView();
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
