package com.ssw.epicgames.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/error")
public class ErrorController {

    @RequestMapping(value = "/404", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView error404() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error/404");
        return mav;
    }
}
