package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.ReviewEntity;
import com.ssw.epicgames.vos.ReviewVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {
    ReviewVo[] selectReviewsByGameIndex(@Param("gameIndex") int gameIndex);

    int insertReview(ReviewEntity review);

    ReviewEntity selectReviewByIndex(@Param("index") int index);

    int updateReview(ReviewEntity review);

    int selectReviewCount(@Param("gameIndex") int gameIndex);

    ReviewVo[] selectReviews(@Param("gameIndex") int gameIndex,
                             @Param("limitCount") int limitCount,
                             @Param("offsetCount") int offsetCount);


    boolean existedReview(@Param("gameIndex") int gameIndex,
                          @Param("userEmail") String userEmail);

    boolean deletedReview(@Param("gameIndex") int gameIndex,
                          @Param("userEmail") String userEmail);
}
