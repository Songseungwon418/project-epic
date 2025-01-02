package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.MyDTO;
import com.ssw.epicgames.DTO.PayDTO;
import com.ssw.epicgames.DTO.PurchaseDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.PageService;
import com.ssw.epicgames.services.PurchaseService;
import com.ssw.epicgames.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/page")
public class PageController {
    private final PageService pageService;
    private final UserService userService;
    private PurchaseService purchaseService;

    @Autowired
    public PageController(PageService pageService, UserService userService, PurchaseService purchaseService) {

        this.pageService = pageService;
        this.userService = userService;
        this.purchaseService = purchaseService;
    }

    @RequestMapping(value = "/achievement-cover", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getAchievementCover(@RequestParam(value = "index") int index) {
        AchievementEntity achievement = this.pageService.getAchievementByIndex(index);
        if (achievement == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(achievement.getLogo());
    }

    private Map<String, Object> getCommonModelData(UserEntity user) {
        MyDTO[] myDTOs = this.pageService.getUserPurchases(user.getEmail());
        Map<GameEntity, MyDTO[]> gameMap = new HashMap<>();

        for (MyDTO myDTO : myDTOs) {
            GameEntity game = new GameEntity();
            game.setIndex(myDTO.getGameIndex());
            game.setName(myDTO.getGameName());

            if (!gameMap.containsKey(game)) {
                gameMap.put(game, Arrays.stream(myDTOs).filter(x -> x.getGameIndex() == game.getIndex()).toArray(MyDTO[]::new));
            }
        }

        int totalReward = Arrays.stream(myDTOs).mapToInt(MyDTO::getReward).sum();

        // 각 게임에 대한 총 보상 합계를 계산하여 gameScoreMap에 저장
        Map<GameEntity, Integer> gameScoreMap = gameMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Arrays.stream(entry.getValue())
                        .mapToInt(MyDTO::getReward)
                        .sum()
        ));

        Map<String, Object> modelData = new HashMap<>();
        modelData.put("gameMap", gameMap);
        modelData.put("totalReward", totalReward);
        modelData.put("gameScoreMap", gameScoreMap);
        return modelData;
    }

    // http://localhost:8080/page/my
    // http://localhost:8080/page/my?email=mnuw2626@naver.com
    @RequestMapping(value = "/profile", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPage(@SessionAttribute(value = "user", required = false) UserEntity user,
                                  @RequestParam(value = "email", required = false) String email) {
        ModelAndView modelAndView = new ModelAndView();


        //유저의 친구 찾기
        boolean isFriend = this.pageService.isFriend(user.getEmail(), email);
        modelAndView.addObject("isFriend", isFriend);

        if (user == null && email == null) {
            modelAndView.setViewName("redirect:/user/");
            return modelAndView;
        }

        if (email != null) {
            user = this.userService.getUserByEmail(email);
            if (user == null) {
                modelAndView.setStatus(HttpStatusCode.valueOf(404));
                return null;
            }
        }

        UserEntity[] friends = this.pageService.getFriendsByEmail(user.getEmail());
        modelAndView.addObject("friends", friends); // 유저의 친구 가져오기


        Map<String, Object> modelData = getCommonModelData(user);

        modelAndView.addObject("user", user);
        modelAndView.addObject("gameMap", modelData.get("gameMap"));
        modelAndView.addObject("totalReward", modelData.get("totalReward"));
        modelAndView.addObject("gameScoreMap", modelData.get("gameScoreMap"));
        modelAndView.setViewName("pages/My/myPage");
        return modelAndView;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postMyPage(FriendsEntity friend) {
        Result result = this.pageService.insertFriend(friend);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }


    @RequestMapping(value = "/friend", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getFriendPage(@SessionAttribute("user") UserEntity user,
                                      @RequestParam(value = "email", required = false) String email,
                                      @RequestParam(value = "keyword", required = false) String keyword) {
        ModelAndView modelAndView = new ModelAndView();

        //유저의 친구 찾기
        boolean isFriend = this.pageService.isFriend(user.getEmail(), email);
        modelAndView.addObject("isFriend", isFriend);

        if (user == null && email == null) {
            modelAndView.setViewName("redirect:/user/");
            return modelAndView;
        }

        if (email != null) {
            user = this.userService.getUserByEmail(email);
            if (user == null) {
                modelAndView.setStatus(HttpStatusCode.valueOf(404));
                return null;
            }
        }

        if (keyword != null && !keyword.isEmpty()) {
            modelAndView.addObject("keyword", keyword);
        }

        Map<String, Object> modelData = getCommonModelData(user);

        // 유저의 친구 불러오기
        UserEntity[] friends = this.pageService.getFriendsByEmail(user.getEmail());

        // 키워드로 유저 찾기
        UserEntity[] userFriends = this.pageService.getUserByKeyword(keyword);
        modelAndView.addObject("userFriends", userFriends);


        modelAndView.addObject("user", user);
        modelAndView.addObject("gameMap", modelData.get("gameMap"));            //게임 및 업적
        modelAndView.addObject("totalReward", modelData.get("totalReward"));        // 전체 업적의 점수
        modelAndView.addObject("gameScoreMap", modelData.get("gameScoreMap"));
        modelAndView.addObject("friends", friends); // 유저의 친구
        modelAndView.addObject("friendCount", friends.length);  // 친구 수 추가
        modelAndView.setViewName("pages/friends/friend");
        return modelAndView;
    }

    @RequestMapping(value = "/friend", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postFriendPage(FriendsEntity friend) {
        Result result = this.pageService.insertFriend(friend);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/setting", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getSettingPage(
            @SessionAttribute("user") UserEntity user,
            @RequestParam(value = "showPurchaseList", required = false, defaultValue = "false") boolean showPurchaseList
    ) {
        if (user == null) {
            // 세션에 유저 정보가 없을 때 처리
            throw new RuntimeException("User not found in session");
        }
        // db에서 결제 및 구매 내역 가져옴
        List<PayDTO> paylist = this.purchaseService.getPurchasesByUser(user);

        ModelAndView modelAndView = new ModelAndView();
        UserEntity dbUser = this.pageService.getUserByEmail(user.getEmail());
        modelAndView.addObject("user", dbUser);
        modelAndView.addObject("paylist", paylist); // 결제 및 구매 내역 뷰에 넘겨줌
        modelAndView.addObject("showPurchaseList", showPurchaseList); // 결제내역페이지 보여주기 유무

        modelAndView.setViewName("pages/settings/setting");
        return modelAndView;
    }

    @RequestMapping(value = "/setting", method = RequestMethod.POST, produces =  MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postSetting(@SessionAttribute("user") UserEntity user,
                              HttpServletRequest request) throws MessagingException {
        Result result = this.userService.provokeForgotPassword(request, user.getEmail());
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    //region 개인 정보 수정
    @RequestMapping(value = "/setting", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String updateUser(@SessionAttribute("user") UserEntity loginUser,
                             @ModelAttribute UserEntity user) {
        JSONObject response = new JSONObject();
        Result result;
        if (loginUser != null) {
            user.setEmail(loginUser.getEmail());
            result = this.pageService.patchUser(user);
            if (result == CommonResult.SUCCESS) {
                loginUser.setNickname(user.getNickname());
                //TODO 나머지 추가할거 추가하기
            }
        } else {
            result = CommonResult.FAILURE;
        }

        response.put("result", result.nameToLower());
        return response.toString();
    }
    //endregion

    //region 회원 탈퇴
    @RequestMapping(value = "/setting", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteUser(@SessionAttribute("user") UserEntity user, String password) {
        JSONObject response = new JSONObject();
        Result result = this.pageService.deleteUser(user.getEmail(), password);
        response.put("result", result.nameToLower());
        return response.toString();
    }
    //endregion

        @RequestMapping(value = "/privacyPolicy", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPrivacyPolicyPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pages/privacyPolicy");
        return modelAndView;
    }
}
