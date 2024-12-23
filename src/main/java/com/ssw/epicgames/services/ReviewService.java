package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.ReviewEntity;
import com.ssw.epicgames.mappers.ReviewMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.resutls.review.ReviewResult;
import com.ssw.epicgames.vos.ReviewVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewService {
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    public Result writeReview(ReviewEntity review) {
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

        if (this.reviewMapper.existsReview(review.getGameIndex(), review.getUserEmail())) {
            return ReviewResult.FAILURE_DUPLICATE_REVIEW;
        }

        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(null);
        review.setDeletedAt(null);

        return this.reviewMapper.insertReview(review) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public ReviewVo[] getReviewsByGameIndex(int gameIndex) {
        if (gameIndex < 1) {
            return new ReviewVo[0];
        }
        return this.reviewMapper.selectReviewsByGameIndex(gameIndex);
    }

    public CommonResult deleteReview(int index) {
        if (index < 1) {
            return CommonResult.FAILURE;
        }
        ReviewEntity review = this.reviewMapper.selectReviewByIndex(index);
        if (review == null) {
            return CommonResult.FAILURE;
        }
        if (review.getDeletedAt() != null) {
            return CommonResult.FAILURE;
        }
        review.setDeletedAt(LocalDateTime.now());

        return this.reviewMapper.updateReview(review) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public CommonResult modifyReview(int index, int starRating, String content) {
        if (index < 1 || content == null || content.isEmpty() || content.length() > 16777215) {
            return CommonResult.FAILURE;
        }
        ReviewEntity dbReview = this.reviewMapper.selectReviewByIndex(index);
        if (dbReview == null) {
            return CommonResult.FAILURE;
        }
        if (dbReview.getDeletedAt() != null) {
            return CommonResult.FAILURE;
        }
        dbReview.setStar(starRating);
        dbReview.setContent(content);
        dbReview.setUpdatedAt(LocalDateTime.now());
        return this.reviewMapper.updateReview(dbReview) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
}
