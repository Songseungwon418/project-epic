package com.ssw.epicgames.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/purchase")
public class PurchaseController {
    @GetMapping(value = "/cart", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("purchase/cart");
        return mav;
    }
}
