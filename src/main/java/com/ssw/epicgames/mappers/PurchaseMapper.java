package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.CartEntity;
import com.ssw.epicgames.entities.WishlistEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PurchaseMapper {
    //region 장바구니 관련

    /**
     * 장바구니 담기(DB 삽입)
     */
    int insertCart(CartEntity cart);

    /**
     * 장바구니 제거(DB 삭제)
     */
    int deleteCartByIndex(int index);

    /**
     * 유저에 해당하는 장바구니 목록들 조회
     */
    CartEntity[] selectCartByUserEmail(@Param("userEmail") String userEmail);

    /**
     * 유저 이메일 및 게임 번호로 장바구니 조회
     */
    CartEntity selectCartByEmailANDGameIndex(@Param("userEmail") String userEmail,
                                               @Param("gameIndex") int gameIndex);

    //endregion

    //region 위시리스트 관련

    /**
     * 위시리스트 담기(DB 삽입)
     */
    int insertWishlist(WishlistEntity wishlist);

    /**
     * Wishlist 제거(is_deleted 값을 true 로)
     */
    int updateWishlistByIndex(WishlistEntity wishlist);

    /**
     * 유저에 해당하는 Wishlist 목록들 조회
     */
    WishlistEntity[] selectWishlistByUserEmail(@Param("userEmail") String userEmail);

    /**
     * 유저 이메일 및 게임 번호로 Wishlist 조회
     */
    WishlistEntity selectWishlistByEmailANDGameIndex(@Param("userEmail") String userEmail,
                                                       @Param("gameIndex") int gameIndex);

    // endregion
}
