package com.ssw.epicgames.controlleradvice;


import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.PageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

@ControllerAdvice
public class GlobalControllerAdvice {

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

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getError() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", "");
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) throws ModelAndViewDefiningException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.setViewName("error");
        throw new ModelAndViewDefiningException(modelAndView);
    }
}
