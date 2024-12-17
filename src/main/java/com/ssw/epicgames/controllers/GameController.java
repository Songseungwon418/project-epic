package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.GameDTO;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.services.GameService;
import com.ssw.epicgames.services.GenreService;
import com.ssw.epicgames.services.PriceService;
import com.ssw.epicgames.vos.GameVo;
import com.ssw.epicgames.vos.PriceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/game")
public class GameController {

    private final GameService gameService;
    private final GenreService genreService;
    private final PriceService priceService;

    @Autowired
    public GameController(GameService gameService, GenreService genreService, PriceService priceService) {
        this.gameService = gameService;
        this.genreService = genreService;
        this.priceService = priceService;
    }

    @RequestMapping(value = "/cover", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getCover(@RequestParam(value = "index") int index) {
        GameEntity game = this.gameService.getGameByIndex(index);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(game.getMainImage());
    }

    //region 게임 상세 페이지
    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPage(@RequestParam(value = "index") int index) {
        ModelAndView modelAndView = new ModelAndView();
        GameDTO gameDetails = this.gameService.getGameDetails(index);
        PriceVo priceVo = this.priceService.discountInfo(index, gameDetails.getGame().getPrice());
        modelAndView.addObject("gameDetails", gameDetails);
        modelAndView.addObject("priceVo", priceVo);
        modelAndView.setViewName("game/page");
        return modelAndView;
    }

    //region 장르별 페이지
    @RequestMapping(value = "/genre", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGenre(@RequestParam(value = "tag", required = false) String tag,
                                 @RequestParam(value = "keyword", required = false) String keyword) {
        ModelAndView modelAndView = new ModelAndView();

        GenreEntity[] genres = this.genreService.getGenres();
        GenreEntity genre = this.genreService.getGenreByTag(tag);

        modelAndView.addObject("genres", genres);
        modelAndView.addObject("genre", genre);
        if (keyword == null) {
            modelAndView.addObject("games", this.genreService.getGamesByGenre(tag));
        } else {
            modelAndView.addObject("games", this.genreService.getGamesByGenreAndKeyword(tag, keyword));
        }
        modelAndView.setViewName("game/genre");
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
