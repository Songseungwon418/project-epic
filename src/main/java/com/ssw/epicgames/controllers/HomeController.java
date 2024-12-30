package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.PayDTO;
import com.ssw.epicgames.DTO.PurchaseDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.HomeService;
import com.ssw.epicgames.services.PurchaseService;
import com.ssw.epicgames.services.WishlistService;
import com.ssw.epicgames.vos.GameVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/")
public class HomeController {

    private final HomeService homeService;
    private final WishlistService wishlistService;
    private final PurchaseService purchaseService;


    @Autowired
    public HomeController(HomeService homeService, PurchaseService purchaseService, WishlistService wishlistService, PurchaseService purchaseService1) {
        this.homeService = homeService;
        this.wishlistService = wishlistService;
        this.purchaseService = purchaseService1;
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

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();

        GameVo[] newGames = this.homeService.getNewGames();
        GameVo[] saleGames = this.homeService.getSaleGames();

        modelAndView.addObject("newGames", newGames);
        modelAndView.addObject("saleGames", saleGames);

        Map<Integer, Boolean> newGameWishlistStatus = new HashMap<>();
        Map<Integer, Integer> newGameWishlistIndices = new HashMap<>();
        Map<Integer, Boolean> saleGameWishlistStatus = new HashMap<>();
        Map<Integer, Integer> saleGameWishlistIndices = new HashMap<>();
        Set<Integer> purchasedGameIndices = new HashSet<>(); // 빈 Set 초기화

        if (user != null) {
            // 구매 데이터 가져오기
            List<PayDTO> paylist = this.purchaseService.getPurchasesByUser(user);

            purchasedGameIndices = paylist.stream()
                    .filter(Objects::nonNull)
                    .flatMap(payDTO -> payDTO.getPurchase().stream())
                    .map(purchaseDTO -> purchaseDTO.getGame().getIndex())
                    .collect(Collectors.toSet());

            // 위시리스트 데이터 가져오기
            Pair<Map<Integer, Boolean>, Map<Integer, Integer>> newGameWishlistData = wishlistService.getWishlistData(newGames, user);
            Pair<Map<Integer, Boolean>, Map<Integer, Integer>> saleGameWishlistData = wishlistService.getWishlistData(saleGames, user);

            newGameWishlistStatus = newGameWishlistData.getLeft();
            newGameWishlistIndices = newGameWishlistData.getRight();

            saleGameWishlistStatus = saleGameWishlistData.getLeft();
            saleGameWishlistIndices = saleGameWishlistData.getRight();
        }

        modelAndView.addObject("newGameWishlistStatus", newGameWishlistStatus);
        modelAndView.addObject("newGameWishlistIndices", newGameWishlistIndices);
        modelAndView.addObject("saleGameWishlistStatus", saleGameWishlistStatus);
        modelAndView.addObject("saleGameWishlistIndices", saleGameWishlistIndices);
        modelAndView.addObject("purchasedGameIndices", purchasedGameIndices);
        modelAndView.addObject("user", user);

        modelAndView.setViewName("home");
        return modelAndView;
    }
}
