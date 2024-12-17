package com.ssw.epicgames.mappers;

import com.ssw.epicgames.DTO.MyDTO;
import com.ssw.epicgames.entities.AchievementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AchievementMapper {
    AchievementEntity getAchievementByIndex(@Param("index") int index);

    List<AchievementEntity> getAchievementByUserEmail(@Param("userEmail") String userEmail);

    MyDTO[] selectMyDTOs(@Param("userEmail") String userEmail);
}
