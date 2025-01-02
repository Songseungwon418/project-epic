package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.GameDTO;
import com.ssw.epicgames.DTO.PayDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.*;
import com.ssw.epicgames.vos.GameVo;
import com.ssw.epicgames.vos.PriceVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/game")
public class GameController {

    private final GameService gameService;
    private final GenreService genreService;
    private final PriceService priceService;
    private final WishlistService wishlistService;
    private final CartService cartService;
    private final PurchaseService purchaseService;

    @Autowired
    public GameController(GameService gameService, GenreService genreService, PriceService priceService, WishlistService wishlistService, CartService cartService, PurchaseService purchaseService) {
        this.gameService = gameService;
        this.genreService = genreService;
        this.priceService = priceService;
        this.wishlistService = wishlistService;
        this.cartService = cartService;
        this.purchaseService = purchaseService;
    }

    //region 게임검색결과
    @RequestMapping(value = "/search-game", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<GameVo[]> getSearchGame(@RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(new GameVo[0]);
        }

        try {
            GameVo[] games = gameService.getGamesByKeyword(keyword);

            if (games == null || games.length == 0) {
                return ResponseEntity.ok(new GameVo[0]);
            }

            return ResponseEntity.ok(games);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GameVo[0]);
        }
    }
    //endregion

    //region 마이페이지 이미지
    @RequestMapping(value = "/cover", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getCover(@RequestParam(value = "index") int index) {
        GameEntity game = this.gameService.getGameByIndex(index);
        if (game == null) {
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
    //endregion

    //region 게임 상세 페이지 이미지
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "index") int index,
                                           @RequestParam(value = "type") String type,
                                           @RequestParam(value = "itemIndex", required = false) Integer itemIndex) {
        byte[] imageData = null;
        String contentType = "image/jpeg";

        GameDTO gameDetails = this.gameService.getGameDetails(index);

        switch (type) {
            case "gameImage":
                GameEntity game = gameDetails.getGame();
                if (game != null && itemIndex != null && itemIndex >= 0 && itemIndex < 4) {
                    MediaEntity[] media = gameDetails.getMedia();
                    if (media != null && itemIndex < media.length) {
                        imageData = media[itemIndex].getImage();
                    }
                }
                break;

            case "gameLogo":
                GameEntity gameWithLogo = gameDetails.getGame();
                if (gameWithLogo != null) {
                    imageData = gameWithLogo.getMainLogo();
                    contentType = "image/png";
                }
                break;

            case "category":
                CategoryEntity[] categories = gameDetails.getCategory();
                if (categories != null && itemIndex != null && itemIndex >= 0 && itemIndex < categories.length) {
                    imageData = categories[itemIndex].getImage();
                    contentType = "image/png";
                }
                break;

            case "achievement":
                AchievementEntity[] achievements = gameDetails.getAchievement();
                if (achievements != null && itemIndex != null && itemIndex >= 0 && itemIndex < achievements.length) {
                    imageData = achievements[itemIndex].getLogo();
                    contentType = "image/jpeg";
                }
                break;

            case "gameRating":
                GameRatingEntity gameRating = gameDetails.getGameRating();
                if (gameRating != null) {
                    imageData = gameRating.getLogo();
                    contentType = "image/png";
                }
                break;

            default:
                return ResponseEntity.badRequest().build(); // 잘못된 요청
        }

        if (imageData == null) {
            return ResponseEntity.notFound().build(); // 이미지가 없으면 404 반환
        }

        String eTag = String.valueOf(gameDetails.hashCode()); // ETag 설정
        return ResponseEntity
                .ok()
                .eTag(eTag) // ETag를 응답에 추가
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES)) // 클라이언트에게 30분 동안 캐시하도록 지시
                .header("Content-Type", contentType)
                .body(imageData);
    }
    //endregion

    //region 게임 상세 페이지
    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPage(@RequestParam(value = "index") int index,
                                @SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();

        GameDTO gameDetails = this.gameService.getGameDetails(index);
        PriceVo priceVo = this.priceService.discountInfo(index, gameDetails.getGame().getPrice());
        Integer purchaseIndex = this.gameService.getPurchaseIndex(user, index);

        boolean isInWishlist = this.wishlistService.isInWishlist(index, user);
        Integer wishlistIndex = this.wishlistService.getWishlistIndex(index, user);

        boolean isInCart = this.cartService.isInCart(index, user);
        Integer cartIndex = this.cartService.getCartIndex(index, user);

        boolean isAdult = false;
        if (user != null && user.getBirthdate() != null) {
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(user.getBirthdate(), currentDate).getYears();
            isAdult = age >= 19;
        }

        modelAndView.addObject("user", user);
        modelAndView.addObject("gameDetails", gameDetails);
        modelAndView.addObject("priceVo", priceVo);
        modelAndView.addObject("purchaseIndex", purchaseIndex);
        modelAndView.addObject("isInWishlist", isInWishlist);
        modelAndView.addObject("wishlistIndex", wishlistIndex);
        modelAndView.addObject("isInCart", isInCart);
        modelAndView.addObject("cartIndex", cartIndex);
        modelAndView.addObject("isAdult", isAdult);

        modelAndView.setViewName("game/page");
        return modelAndView;
    }

    //endregion

    //region 장르별 페이지 이미지
    // 전체 게임 이미지 조회
    @RequestMapping(value = "/genre-image-all", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getAllGameImageByGenre(@RequestParam(value = "index") int index,
                                                         @RequestParam(value = "tag") String tag) {
        GameVo[] games = this.genreService.getGamesByGenre(tag);

        if (index < 0 || index >= games.length) {
            return ResponseEntity.notFound().build();
        }

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

    // 키워드로 검색된 게임 이미지 조회
    @RequestMapping(value = "/genre-image-search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getSearchGameImageByGenre(@RequestParam(value = "index") int index,
                                                            @RequestParam(value = "keyword") String keyword,
                                                            @RequestParam(value = "tag") String tag) {
        GameVo[] games = this.genreService.getGamesByGenreAndKeyword(tag, keyword);

        if (index < 0 || index >= games.length) {
            return ResponseEntity.notFound().build();
        }

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
    //endregion

    //region 장르별 페이지
    @RequestMapping(value = "/genre", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGenre(@RequestParam(value = "tag", required = false) String tag,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();

        GenreEntity[] genres = this.genreService.getGenres();
        GenreEntity genre = this.genreService.getGenreByTag(tag);
        modelAndView.addObject("genres", genres);
        modelAndView.addObject("genre", genre);

        GameVo[] games = (keyword == null || keyword.isEmpty())
                ? this.genreService.getGamesByGenre(tag)
                : this.genreService.getGamesByGenreAndKeyword(tag, keyword);
        modelAndView.addObject("games", games);

        List<GameVo> onSaleGames = this.gameService.getOnSaleGames();
        modelAndView.addObject("onSaleGames", onSaleGames);

        if (keyword != null && !keyword.isEmpty()) {
            modelAndView.addObject("keyword", keyword);
        }

        Map<Integer, Boolean> gameWishlistStatus = new HashMap<>();
        Map<Integer, Integer> gameWishlistIndices = new HashMap<>();
        Set<Integer> purchasedGameIndices = new HashSet<>();

        if (user != null && games != null) {
            WishlistDTO[] wishlists = this.purchaseService.getWishlists(user);
            List<PayDTO> paylist = this.purchaseService.getPurchasesByUser(user);

            purchasedGameIndices = paylist.stream()
                    .filter(Objects::nonNull)
                    .flatMap(payDTO -> payDTO.getPurchase().stream())
                    .map(purchaseDTO -> purchaseDTO.getGame().getIndex())
                    .collect(Collectors.toSet());

            if (wishlists != null) {
                gameWishlistStatus = this.wishlistService.getWishlistStatus(games, wishlists);
                gameWishlistIndices = this.wishlistService.getWishlistIndices(games, wishlists);
            }
        }

        modelAndView.addObject("gameWishlistStatus", gameWishlistStatus);
        modelAndView.addObject("gameWishlistIndices", gameWishlistIndices);
        modelAndView.addObject("purchasedGameIndices", purchasedGameIndices);
        modelAndView.addObject("user", user);

        modelAndView.setViewName("game/genre");
        return modelAndView;
    }

    //endregion

    //region 찾아보기 페이지 이미지
    // 전체 게임 이미지 조회
    @RequestMapping(value = "/browse-image-all", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getAllGameImage(@RequestParam(value = "index") int index) {
        GameVo game = this.gameService.selectGameByIndex(index);
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

    // 키워드로 검색된 게임 이미지 조회
    @RequestMapping(value = "/browse-image-search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getSearchGameImage(@RequestParam(value = "index") int index,
                                                     @RequestParam(value = "keyword") String keyword) {
        GameVo[] games = this.gameService.getGamesByKeyword(keyword);
        if (index < 0 || index >= games.length) {
            return ResponseEntity.notFound().build();
        }
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
    //endregion

    //region 찾아보기 페이지
    @RequestMapping(value = "/browse", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getBrowse(@RequestParam(value = "keyword", required = false) String keyword,
                                  @SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();

        GameVo[] games = (keyword == null || keyword.isEmpty())
                ? this.gameService.getAllGames()
                : this.gameService.getGamesByKeyword(keyword);
        modelAndView.addObject("games", games);

        List<GameVo> onSaleGames = this.gameService.getOnSaleGames();
        modelAndView.addObject("onSaleGames", onSaleGames);

        if (keyword != null && !keyword.isEmpty()) {
            modelAndView.addObject("keyword", keyword);
        }

        Map<String, Object> wishlistData = this.wishlistService.getWishlistStatus(user, games);
        Set<Integer> purchasedGameIndices = new HashSet<>();

        if (user != null) {
            List<PayDTO> paylist = this.purchaseService.getPurchasesByUser(user);

            purchasedGameIndices = paylist.stream()
                    .filter(Objects::nonNull)
                    .flatMap(payDTO -> payDTO.getPurchase().stream())
                    .map(purchaseDTO -> purchaseDTO.getGame().getIndex())
                    .collect(Collectors.toSet());
        }

        modelAndView.addAllObjects(wishlistData);
        modelAndView.addObject("purchasedGameIndices", purchasedGameIndices);
        modelAndView.addObject("user", user);
        modelAndView.setViewName("game/browse");
        return modelAndView;
    }
    //endregion

    //region 게임 추가
    /** 게임 추가 화면 페이지*/
    @GetMapping(value = "/add-game", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView addGame() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("game/add-game");
        return mav;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addGame(@ModelAttribute GameEntity game) {
        System.out.println(game.getName());
        System.out.println(game.getGrGrac());
        System.out.println(game.getCompany());
        System.out.println(game.getOpenDate());
        System.out.println(game.getMainImage());
        System.out.println(game.getMainLogo());

        Result result = CommonResult.SUCCESS;

        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    //endregion
}
