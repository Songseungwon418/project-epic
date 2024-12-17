package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.CartEntity;
import com.ssw.epicgames.entities.PurchaseEntity;
import com.ssw.epicgames.entities.WishlistEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    CartEntity[] selectCartByUserEmail(@Param("user_email") String user_email);

    /**
     * 유저 이메일 및 게임 번호로 장바구니 조회
     */
    CartEntity selectCartByEmailANDGameIndex(@Param("userEmail") String userEmail,
                                               @Param("gameIndex") int gameIndex);

    /** 장바구니 index로 장바구니 조회 */
    CartEntity selectCartByIndex(@Param("index") int index);

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

    /** 위시리스트 index로 위시리스트 조회(제거되지않은 목록만) */
    WishlistEntity selectWishlistByIndex(@Param("index") int index);

    /** 위시리스트 index로 위시리스트 조회(제거된 목록까지 포함) */
    WishlistEntity selectAllWishlistByIndex(@Param("index") int index);

    int insertPurchase(PurchaseEntity purchase);

    // endregion

    List<PurchaseEntity> getPurchasesByUserEmail(@Param("userEmail") String userEmail);
//    PurchaseEntity[] selectPurchaseByUserEmail(@Param("userEmail") String userEmail);
}
