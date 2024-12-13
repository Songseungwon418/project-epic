package com.ssw.epicgames.controllers;

import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.services.GameService;
import com.ssw.epicgames.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/game")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService, GameService gameService) {
        this.genreService = genreService;
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
}
