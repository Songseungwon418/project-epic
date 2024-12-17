package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.PurchaseDTO;
import com.ssw.epicgames.entities.PurchaseEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.PageService;
import com.ssw.epicgames.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value = "/page")
public class PageController {
    private final PageService pageService;
    private final UserService userService;

    @Autowired
    public PageController(PageService pageService, UserService userService) {

        this.pageService = pageService;
        this.userService = userService;
    }

    @RequestMapping(value = "/my", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPage(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        if (user == null) {
            // 세션에 유저 정보가 없을 때 처리
            throw new RuntimeException("User not found in session");
        }

        // 세션에서 가져온 user 이메일을 사용하여 구매 정보 가져오기
        PurchaseDTO purchaseDTO = this.pageService.getUserPurchases(user.getEmail());
        modelAndView.addObject("purchaseDTO", purchaseDTO);
        modelAndView.setViewName("pages/My/myPage");
        return modelAndView;
    }

    @RequestMapping(value = "/friend", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getFriendPage(@SessionAttribute("user") UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        UserEntity[] users = this.pageService.getFriendsByEmail(user.getEmail());
        modelAndView.addObject("users", users);
        modelAndView.addObject("friendCount", users.length);  // 친구 수 추가
        modelAndView.setViewName("pages/friends/friend");
        return modelAndView;
    }
}
