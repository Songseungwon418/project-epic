package com.ssw.epicgames.controllers;

import com.ssw.epicgames.services.HomeService;
import com.ssw.epicgames.vos.GameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/")
public class HomeController {

    private final HomeService homeService;

    @Autowired
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @RequestMapping(value = "/new-game-image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getNewGameImage(@RequestParam(value = "index") int index) {
        GameVo game = this.homeService.getGameByIndex(index, false);
        if (game == null || game.getMainImage() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(game.getMainImage());
    }

    @RequestMapping(value = "/sale-game-image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getSaleGameImage(@RequestParam(value = "index") int index) {
        GameVo game = this.homeService.getGameByIndex(index, true);
        if (game == null || game.getMainImage() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(game.getMainImage());
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex() {
        ModelAndView modelAndView = new ModelAndView();

        GameVo[] newGames =  this.homeService.getNewGames();
        GameVo[] saleGames = this.homeService.getSaleGames();

        modelAndView.addObject("newGames", newGames);
        modelAndView.addObject("saleGames", saleGames);

        modelAndView.setViewName("home");
        return modelAndView;
    }

}
