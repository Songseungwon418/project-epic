package com.ssw.epicgames.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getError() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", "");
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
