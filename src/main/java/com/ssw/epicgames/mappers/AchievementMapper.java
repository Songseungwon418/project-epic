package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.AchievementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AchievementMapper {
//    AchievementEntity getAchievementsByIndex(@Param("index") int index);
    List<AchievementEntity> getAchievementByUserEmail(@Param("userEmail") String userEmail);
}
