package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.DiscountEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiscountMapper {

    /** 게임 index로 현재 날짜에 해당하는 할인 정보 조회 */
    DiscountEntity selectDiscountByGameIndex(int gameIndex);
}
