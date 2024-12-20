package com.ssw.epicgames.controllers;

import com.ssw.epicgames.entities.ReviewEntity;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.services.ReviewService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "review")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @RequestMapping(value = "/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(ReviewEntity review) {
        CommonResult result = this.reviewService.writeReview(review);
        JSONObject response = new JSONObject();
        response.put("result", result.name().toLowerCase());
        return response.toString();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewEntity[]> getIndex(@RequestParam(value = "gameIndex", required = false, defaultValue = "0") int gameIndex) {
        ReviewEntity[] reviews = this.reviewService.getReviewsByGameIndex(gameIndex);
        if (reviews == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(reviews);
    }
}
