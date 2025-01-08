package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.PayDTO;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.services.GameService;
import com.ssw.epicgames.services.HomeService;
import com.ssw.epicgames.services.PurchaseService;
import com.ssw.epicgames.services.WishlistService;
import com.ssw.epicgames.vos.GameVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping(value = "/")
public class HomeController {

    private final HomeService homeService;
    private final WishlistService wishlistService;
    private final PurchaseService purchaseService;
    private final GameService gameService;


    @Autowired
    public HomeController(HomeService homeService, PurchaseService purchaseService, WishlistService wishlistService, GameService gameService) {
        this.homeService = homeService;
        this.wishlistService = wishlistService;
        this.purchaseService = purchaseService;
        this.gameService = gameService;
    }

    @RequestMapping(value = "/new-game-image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getNewGameImage(@RequestParam(value = "index") int index) {
        GameVo game = this.homeService.getGameByIndex(index, false);
        return getResponseEntity(game);
    }

    @RequestMapping(value = "/sale-game-image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getSaleGameImage(@RequestParam(value = "index") int index) {
        GameVo game = this.homeService.getGameByIndex(index, true);
        return getResponseEntity(game);
    }

    // 이미지 응답을 메서드로 빼서 사용
    static ResponseEntity<byte[]> getResponseEntity(GameVo game) {
        if (game == null || game.getMainImage() == null) {
            return ResponseEntity.notFound().build();
        }
        String eTag = String.valueOf(game.hashCode()); // ETag 설정
        return ResponseEntity
                .ok()
                .eTag(eTag) // ETag를 응답에 추가
                .cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES)) // 클라이언트에게 10분 동안 캐시하도록 지시
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
