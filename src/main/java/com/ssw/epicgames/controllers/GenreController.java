package com.ssw.epicgames.controllers;

import com.ssw.epicgames.entities.GenreEntity;
import com.ssw.epicgames.services.GameService;
import com.ssw.epicgames.services.GenreService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/genre")
public class GenreController {

    private final GenreService genreService;
    @Autowired
    public GenreController(GenreService genreService, GameService gameService) {
        this.genreService = genreService;
    }

    /** DB에서 게임 등급 가져와서 클라이언트에 넘겨줌 */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGenreList() {
        GenreEntity[] genres = this.genreService.getGenres();
        if (genres == null) {
            return "";
        }
        JSONObject response = new JSONObject();
        response.put("genres", genres);
        return response.toString();
    }
}
