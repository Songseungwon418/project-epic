package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.PayDTO;
import com.ssw.epicgames.DTO.PurchaseDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.GameService;
import com.ssw.epicgames.services.HomeService;
import com.ssw.epicgames.services.PurchaseService;
import com.ssw.epicgames.services.WishlistService;
import com.ssw.epicgames.vos.GameVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/")
public class HomeController {

    private final HomeService homeService;
    private final WishlistService wishlistService;
    private final PurchaseService purchaseService;
    private final GameService gameService;


    @Autowired
    public HomeController(HomeService homeService, WishlistService wishlistService, PurchaseService purchaseService1, GameService gameService) {
        this.homeService = homeService;
        this.wishlistService = wishlistService;
        this.purchaseService = purchaseService1;
        this.gameService = gameService;
    }

    @RequestMapping(value = "/new-game-image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getNewGameImage(@RequestParam(value = "index") int index) {
        GameVo game = this.homeService.getGameByIndex(index, false);
        if (game == null || game.getMainImage() == null) {
            return ResponseEntity.notFound().build();
        }
        String eTag = String.valueOf(game.hashCode()); // ETag 설정
        return ResponseEntity
                .ok()
                .eTag(eTag) // ETag를 응답에 추가
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)) // 클라이언트에게 30분 동안 캐시하도록 지시
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
        String eTag = String.valueOf(game.hashCode()); // ETag 설정
        return ResponseEntity
                .ok()
                .eTag(eTag) // ETag를 응답에 추가
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)) // 클라이언트에게 30분 동안 캐시하도록 지시
                .header("Content-Type", "image/jpeg")
                .body(game.getMainImage());
    }

    @RequestMapping(value = "/popular-game-image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getPopularGameImage(@RequestParam(value = "index") int index) {
        GameVo[] games = this.homeService.getPopularGames();
        GameVo game = games[index];

        if (game == null || game.getMainImage() == null) {
            return ResponseEntity.notFound().build();
        }

        String eTag = String.valueOf(game.hashCode()); // ETag 설정
        return ResponseEntity
                .ok()
                .eTag(eTag) // ETag를 응답에 추가
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)) // 클라이언트에게 30분 동안 캐시하도록 지시
                .header("Content-Type", "image/jpeg")
                .body(game.getMainImage());
    }

    @RequestMapping(value = "/play-game-image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getPlayGameImage(@RequestParam(value = "index") int index) {
        GameVo[] games = this.homeService.getPlayGames();
        GameVo game = games[index];

        if (game == null || game.getMainImage() == null) {
            return ResponseEntity.notFound().build();
        }

        String eTag = String.valueOf(game.hashCode()); // ETag 설정
        return ResponseEntity
                .ok()
                .eTag(eTag) // ETag를 응답에 추가
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)) // 클라이언트에게 30분 동안 캐시하도록 지시
                .header("Content-Type", "image/jpeg")
                .body(game.getMainImage());
    }

    @RequestMapping(value = "/free-game-image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getFreeGameImage(@RequestParam(value = "index") int index) {
        GameVo[] games = this.homeService.getFreeGames();
        GameVo game = games[index];

        if (game == null || game.getMainImage() == null) {
            return ResponseEntity.notFound().build();
        }

        String eTag = String.valueOf(game.hashCode()); // ETag 설정
        return ResponseEntity
                .ok()
                .eTag(eTag) // ETag를 응답에 추가
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)) // 클라이언트에게 30분 동안 캐시하도록 지시
                .header("Content-Type", "image/jpeg")
                .body(game.getMainImage());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();

        GameVo[] newGames = this.homeService.getNewGames();
        GameVo[] saleGames = this.homeService.getSaleGames();
        GameVo[] popularGames = this.homeService.getPopularGames();
        GameVo[] playGames = this.homeService.getPlayGames();
        GameVo[] freeGames = this.homeService.getFreeGames();

        modelAndView.addObject("newGames", newGames);
        modelAndView.addObject("saleGames", saleGames);
        modelAndView.addObject("popularGames", popularGames);
        modelAndView.addObject("playGames", playGames);
        modelAndView.addObject("freeGames", freeGames);

        List<GameVo> onSaleGames = this.gameService.getOnSaleGames();
        modelAndView.addObject("onSaleGames", onSaleGames);

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
