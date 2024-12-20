package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.ReviewEntity;
import com.ssw.epicgames.mappers.ReviewMapper;
import com.ssw.epicgames.resutls.CommonResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewService {
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    public CommonResult writeReview(ReviewEntity review) {
        if (review == null) {
         return CommonResult.FAILURE;
        }
        if (review.getGameIndex() < 1) {
            return CommonResult.FAILURE;
        }
        if (review.getStar() < 1 || review.getStar() > 5) {
            return CommonResult.FAILURE;
        }
        if (review.getContent() == null || review.getContent().isEmpty() || review.getContent().length() > 16777215) {
            return CommonResult.FAILURE;
        }

        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(null);
        review.setDeletedAt(null);

        return this.reviewMapper.insertReview(review) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public ReviewEntity[] getReviewsByGameIndex(int gameIndex) {
        if (gameIndex < 1) {
            return null;
        }
        return this.reviewMapper.selectReviewsByGameIndex(gameIndex);
    }
}
