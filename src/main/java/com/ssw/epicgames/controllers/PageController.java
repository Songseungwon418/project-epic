package com.ssw.epicgames.controllers;

import com.ssw.epicgames.DTO.MyDTO;
import com.ssw.epicgames.DTO.PayDTO;
import com.ssw.epicgames.DTO.PurchaseDTO;
import com.ssw.epicgames.entities.AchievementEntity;
import com.ssw.epicgames.entities.GameEntity;
import com.ssw.epicgames.entities.PurchaseEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.PageService;
import com.ssw.epicgames.services.PurchaseService;
import com.ssw.epicgames.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

        Map<GameEntity, Integer> gameScoreMap = gameMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Arrays.stream(entry.getValue()).mapToInt(MyDTO::getReward).sum()
        ));

        Map<String, Object> modelData = new HashMap<>();
        modelData.put("gameMap", gameMap);
        modelData.put("totalReward", totalReward);
        modelData.put("gameScoreMap", gameScoreMap);
        return modelData;
    }

    @RequestMapping(value = "/my", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPage(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        if (user == null) {
            throw new RuntimeException("User not found in session");
        }
        // 공통 로직 처리
        Map<String, Object> modelData = getCommonModelData(user);

        // 추가적으로 필요한 데이터 처리
        UserEntity dbGetNickname = this.pageService.getUserByEmail(user.getEmail());
        modelAndView.addObject("dbGetNickname", dbGetNickname);

        modelAndView.addObject("gameMap", modelData.get("gameMap"));
        modelAndView.addObject("totalReward", modelData.get("totalReward"));
        modelAndView.addObject("gameScoreMap", modelData.get("gameScoreMap"));
        modelAndView.setViewName("pages/My/myPage");
        return modelAndView;
    }


    @RequestMapping(value = "/friend", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getFriendPage(@SessionAttribute("user") UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        if (user == null) {
            throw new RuntimeException("User not found in session");
        }

        // 공통 로직 처리
        Map<String, Object> modelData = getCommonModelData(user);

        // 추가적으로 필요한 데이터 처리
        UserEntity dbGetNickname = this.pageService.getUserByEmail(user.getEmail());
        UserEntity[] users = this.pageService.getFriendsByEmail(user.getEmail());

        modelAndView.addObject("dbGetNickname", dbGetNickname);
        modelAndView.addObject("users", users);
        modelAndView.addObject("gameMap", modelData.get("gameMap"));
        modelAndView.addObject("totalReward", modelData.get("totalReward"));
        modelAndView.addObject("gameScoreMap", modelData.get("gameScoreMap"));
        modelAndView.addObject("friendCount", users.length);  // 친구 수 추가
        modelAndView.setViewName("pages/friends/friend");
        return modelAndView;
    }

    @RequestMapping(value ="/setting", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getSettingPage(@SessionAttribute("user") UserEntity user) {
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

        modelAndView.setViewName("pages/settings/setting");
        return modelAndView;
    }

    //region 개인 정보 수정
    @RequestMapping(value = "/setting", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String updateUser(@SessionAttribute("user") UserEntity loginUser,
                             @ModelAttribute UserEntity user) {
        JSONObject response = new JSONObject();
        Result result;
        if(loginUser != null) {
            result = this.pageService.patchUser(user);
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
}
