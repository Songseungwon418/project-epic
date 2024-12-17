package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.MyDTO;
import com.ssw.epicgames.DTO.PurchaseDTO;
import com.ssw.epicgames.entities.AchievementEntity;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.PurchaseEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.PageService;
import com.ssw.epicgames.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

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

    @RequestMapping(value = "/achievement-cover", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getAchievementCover(@RequestParam(value = "index") int index) {
        AchievementEntity achievement = this.pageService.getAchievementByIndex(index);
        if (achievement == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(achievement.getLogo());
    }

    @RequestMapping(value = "/my", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPage(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        if (user == null) {
            // 세션에 유저 정보가 없을 때 처리
            throw new RuntimeException("User not found in session");
        }

        MyDTO[] myDTOs = this.pageService.getUserPurchases(user.getEmail());
        Map<GameEntity, MyDTO[]> gameMap = new HashMap<>();
        for (MyDTO myDTO : myDTOs) {
            GameEntity game = new GameEntity();
            game.setIndex(myDTO.getGameIndex());
            game.setName(myDTO.getGameName());
            if (!gameMap.containsKey(game)) {
                gameMap.put(game, Arrays.stream(myDTOs).filter((x) -> x.getGameIndex() == game.getIndex()).toArray(MyDTO[]::new));
            }
        }

        modelAndView.addObject("gameMap", gameMap);
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
