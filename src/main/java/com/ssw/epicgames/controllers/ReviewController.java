package com.ssw.epicgames.controllers;

import com.ssw.epicgames.entities.ReviewEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.services.ReviewService;
import com.ssw.epicgames.vos.PageVo;
import com.ssw.epicgames.vos.ReviewVo;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @RequestMapping(value = "/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(ReviewEntity review) {
        Result result = this.reviewService.writeReview(review);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        return response.toString();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getReviews(
            @RequestParam(value = "gameIndex", required = false, defaultValue = "0") int gameIndex,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        if (gameIndex <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid gameIndex"));
        }

        Pair<PageVo, ReviewVo[]> result = this.reviewService.getReviews(page, gameIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("pageVo", result.getLeft());
        response.put("reviews", result.getRight());

        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteIndex(@RequestParam(value = "index", required = false, defaultValue = "0") int index) {
        CommonResult result = this.reviewService.deleteReview(index);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        return response.toString();
    }

    @RequestMapping(value = "/", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchIndex(@RequestParam(value = "index", required = false, defaultValue = "0") int index,
                             @RequestParam(value = "starRating", required = false) int starRating,
                             @RequestParam(value = "content", required = false) String content) {
        CommonResult result = this.reviewService.modifyReview(index, starRating, content);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        return response.toString();
    }

}

