package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.DiscountEntity;
import com.ssw.epicgames.mappers.DiscountMapper;
import com.ssw.epicgames.vos.PriceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceService {
    private final DiscountMapper discountMapper;

    @Autowired
    public PriceService(DiscountMapper discountMapper) {
        this.discountMapper = discountMapper;
    }

    /** 게임 index로 해당하는 게임의 할인 정보와 가격 정보를 넘겨줌
     * @param gameIndex int, 게임 번호
     * @param gamePrice int, 게임 가격
     * */
    public PriceVo discountInfo(int gameIndex, int gamePrice) {
        if (gameIndex < 0 || gamePrice < 0) {
            return null;
        }

        DiscountEntity discount = this.discountMapper.selectDiscountByGameIndex(gameIndex);

        // 할인 정보가 있으면
        if (discount != null) {
            // 할인률 가져와서 넣어서 가격 객체 생성 -> 생성 시 할인 가격 및 할인 적용된 가격 계산함
            PriceVo prices = new PriceVo(gamePrice, discount.getDiscountPercent());
            // 가져온 할인 정보도 설정해줌
            prices.setId(discount.getId());
            prices.setCode(discount.getCode());
            prices.setStartDate(discount.getStartDate());
            prices.setEndDate(discount.getEndDate());
            prices.setActive(discount.isActive());
            return prices; // 설정한 가격 정보 반환
        }
        // 할인 정보가 없을 시(할인 안 함)
        else {
            // 할인률을 0으로 설정하여 가격 정보 객체 생성 및 반환(할인 정보는 없음, null)
            return new PriceVo(gamePrice, 0);
        }
    }
}
