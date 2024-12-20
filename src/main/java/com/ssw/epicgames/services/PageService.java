package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.MyDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.mappers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



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

    public AchievementEntity getAchievementByIndex(int index) {
        if (index < 1) return null;
        return this.achievementMapper.getAchievementByIndex(index);
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


    public MyDTO[] getUserPurchases(String email) {
        return this.achievementMapper.selectMyDTOs(email);
    }

}
