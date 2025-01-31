package com.ssw.epicgames.controlleradvice;

import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.PurchaseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final PurchaseService purchaseService;

    @Autowired
    public GlobalControllerAdvice(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @ModelAttribute("isLoggedIn")
    public boolean addIsLoggedIn(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        return user != null;
    }

    @ModelAttribute("userNickname")
    public String addUserNickname(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        return user != null ? user.getNickname() : null;
    }

    @ModelAttribute("userEmail")
    public String addUserEmail(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        return user != null ? user.getEmail() : null;
    }

    @ModelAttribute("cartCount")
    public int addCartCount(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return 0;
        }
        return this.purchaseService.getCartCount(user);
    }


    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) throws ModelAndViewDefiningException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.setViewName("error");
        throw new ModelAndViewDefiningException(modelAndView);
    }
}
