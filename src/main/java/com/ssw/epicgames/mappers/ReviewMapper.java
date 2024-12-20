package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.ReviewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {
    ReviewEntity[] selectReviewsByGameIndex(@Param("gameIndex") int gameIndex);

    int insertReview(ReviewEntity review);
}
