package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.PurchaseDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.mappers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class PageService {
    private final AchievementMapper achievementMapper;
    private final GameService gameService;
    private final GameMapper gameMapper;
    private final PurchaseMapper purchaseMapper;
    private final UserMapper userMapper;
    private final FriendMapper friendMapper;

    @Autowired
    public PageService(AchievementMapper achievementMapper, GameService gameService, GameMapper gameMapper, PurchaseMapper purchaseMapper, UserMapper userMapper, FriendMapper friendMapper) {
        this.achievementMapper = achievementMapper;
        this.gameService = gameService;
        this.gameMapper = gameMapper;
        this.purchaseMapper = purchaseMapper;
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
    }

    public UserEntity[] getFriendsByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        FriendsEntity[] friends = this.friendMapper.selectFriendsByUserEmail(email);    // 해당 이메일의 친구 목록
        UserEntity[] users = new UserEntity[friends.length];        // 친구의 수만큼 배열 생성

        if (friends.length > 0) {
            for (int i = 0; i < friends.length; i++) {
                FriendsEntity friend = friends[i];
                users[i] = this.userMapper.selectUserByEmail(friend.getFriend_email());
            }
        }
        return users;
    }

    public PurchaseDTO getUserPurchases(String email) {
        // 사용자가 구매한 게임 및 업정 정보 가져오기
        List<PurchaseEntity> purchases = this.purchaseMapper.getPurchasesByUserEmail(email);
        List<GameEntity> games = this.gameMapper.getGamesByUserEmail(email);
        List<AchievementEntity> achievements = this.achievementMapper.getAchievementByUserEmail(email);

        PurchaseDTO purchaseDTO = PurchaseDTO.builder()
                .purchase(purchases.toArray(new PurchaseEntity[0]))
                .game(games.toArray(new GameEntity[0]))
                .achievement(achievements.toArray(new AchievementEntity[0]))
                .build();

        return purchaseDTO;
    }

//    public PurchaseDTO[] getPurchasesByEmail(String email) {
//        if (email == null || email.isEmpty()) {
//            return null;
//        }
//
//        PurchaseEntity[] purchases = this.purchaseMapper.selectPurchaseByUserEmail(email);
//        PurchaseDTO[] purchaseDTOs = new PurchaseDTO[purchases.length];
//        GameEntity[] games = new GameEntity[purchases.length];
//
//        for (int i = 0; i < purchases.length; i++) {
//            PurchaseEntity purchase = purchases[i];
//            games[i] = this.gameService.getGameByIndex(purchase.getGameIndex());
//            AchievementEntity[] achievements = this.achievementMapper.getAchievementsByIndex(purchase.getGameIndex());
//            purchaseDTOs[i] = PurchaseDTO.builder()
//                    .purchase(purchases)
//                    .game(games[i])
//                    .achievement(achievements)
//                    .build();
//        }
//        return purchaseDTOs;
//    }
}
