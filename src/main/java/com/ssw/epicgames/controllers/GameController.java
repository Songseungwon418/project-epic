package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.GameDTO;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.services.GameService;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    //region 게임 상세 페이지
    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPage(@RequestParam(value = "index") int index) {
        ModelAndView modelAndView = new ModelAndView();
        GameDTO gameDetails = this.gameService.getGameDetails(index);
        modelAndView.addObject("gameDetails", gameDetails);
        modelAndView.setViewName("game/page");
        return modelAndView;
    }
    //endregion

    //region 찾아보기 페이지
    @RequestMapping(value = "/browse", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getBrowse(@RequestParam(value = "keyword", required = false) String keyword) {
        ModelAndView modelAndView = new ModelAndView();
        if (keyword == null || keyword.isEmpty()) {
            GameVo[] games = this.gameService.getAllGames();
            modelAndView.addObject("games", games);
        } else {
            GameVo[] games = this.gameService.getGamesByKeyword(keyword);
            modelAndView.addObject("games", games);
        }
        modelAndView.setViewName("game/browse");
        return modelAndView;
    }
    //endregion
}
