package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.CartEntity;
import com.ssw.epicgames.entities.PayEntity;
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
    CartEntity[] selectCartByUserEmail(@Param("userEmail") String userEmail);

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
    // endregion

    //region 결제 관련

    /** 구매 내역 삽입(하나의 게임에 대한 구매 내역 )*/
    int insertPurchase(PurchaseEntity purchase);

    /** 구매 내역 수정 */
    int updatePurchase(PurchaseEntity purchase);

    /** 게임 index로 해당 유저의 구매 내역 유무 조회 */
    int selectPurchaseByGameIndex(
            @Param("userEmail") String userEmail,
            @Param("gameIndex") int gameIndex);

    /** 구매 id와 게임 index로 해당 구매 내역 조회 */
    PurchaseEntity selectPurchaseByPayIdANDGameIndex(@Param("id") String id, @Param("gameIndex") int gameIndex);

    /** 결제 내역의 id로 구매 내역 검색 */
    List<PurchaseEntity> selectPurchaseByPayId(@Param("payId")String payId);

    /** 유저 이메일로 구매내역들을 조회 */
    List<PurchaseEntity> getPurchasesByUserEmail(@Param("userEmail") String userEmail);

    /** 결제 내역 삽입(결재 정보) */
    int insertPay(PayEntity pay);

    /** 결제 내역 수정 */
    int updatePay(PayEntity pay);

    /** 결제 내역 조회 */
    PayEntity selectPayById(@Param("id")String id);

    /** 유저에 해당하는 결제 내역들 조회*/
    List<PayEntity> selectPayByUser(@Param("userEmail") String userEmail);

    //endregion


//    PurchaseEntity[] selectPurchaseByUserEmail(@Param("userEmail") String userEmail);
}
