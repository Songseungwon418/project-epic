package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.CartDTO;
import com.ssw.epicgames.DTO.GameDTO;
import com.ssw.epicgames.DTO.WishlistDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.services.*;
import com.ssw.epicgames.vos.GameVo;
import com.ssw.epicgames.vos.PriceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/game")
public class GameController {

    private final GameService gameService;
    private final GenreService genreService;
    private final PriceService priceService;
    private final WishlistService wishlistService;
    private final CartService cartService;
    private PurchaseService purchaseService;

    @Autowired
    public GameController(GameService gameService, GenreService genreService, PriceService priceService, WishlistService wishlistService, CartService cartService) {
        this.gameService = gameService;
        this.genreService = genreService;
        this.priceService = priceService;
        this.wishlistService = wishlistService;
        this.cartService = cartService;
    }

    //region 마이페이지 이미지
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

        return ResponseEntity
                .ok()
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

        modelAndView.addObject("gameDetails", gameDetails);
        modelAndView.addObject("priceVo", priceVo);
        modelAndView.addObject("purchaseIndex", purchaseIndex);
        modelAndView.addObject("isInWishlist", isInWishlist);
        modelAndView.addObject("wishlistIndex", wishlistIndex);
        modelAndView.addObject("isInCart", isInCart);
        modelAndView.addObject("cartIndex", cartIndex);

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

        return ResponseEntity
                .ok()
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

        return ResponseEntity
                .ok()
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

        if (keyword != null && !keyword.isEmpty()) {
            modelAndView.addObject("keyword", keyword);
        }

        Map<Integer, Boolean> gameWishlistStatus = new HashMap<>();
        Map<Integer, Integer> gameWishlistIndices = new HashMap<>();

        if (user != null && games != null) {
            WishlistDTO[] wishlists = this.purchaseService.getWishlists(user);

            if (wishlists != null) {
                gameWishlistStatus = this.wishlistService.getWishlistStatus(games, wishlists);
                gameWishlistIndices = this.wishlistService.getWishlistIndices(games, wishlists);
            }
        }

        modelAndView.addObject("gameWishlistStatus", gameWishlistStatus);
        modelAndView.addObject("gameWishlistIndices", gameWishlistIndices);
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
        return ResponseEntity
                .ok()
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
        return ResponseEntity
                .ok()
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

        if (keyword != null && !keyword.isEmpty()) {
            modelAndView.addObject("keyword", keyword);
        }

        Map<String, Object> wishlistData = this.wishlistService.getWishlistStatus(user, games);
        modelAndView.addAllObjects(wishlistData);

        modelAndView.addObject("user", user);

        modelAndView.setViewName("game/browse");
        return modelAndView;
    }
    //endregion
}
