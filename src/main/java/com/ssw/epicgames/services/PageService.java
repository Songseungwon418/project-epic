package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.MyDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.mappers.*;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.resutls.user.DeletedUserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Result patchUser(UserEntity user) {
        if (user == null ||
                user.getEmail() == null || user.getEmail().length() < 8 || user.getEmail().length() > 50 ||
                user.getName() == null || user.getName().isEmpty() || user.getName().length() > 30 ||
                user.getNickname() == null || user.getNickname().length() < 2 || user.getNickname().length() > 10 ||
                user.getPhone() == null || user.getPhone().length() != 11) {
            return CommonResult.FAILURE;
        }

        UserEntity userEntity = this.userMapper.selectUserByEmail(user.getEmail());
        if (userEntity == null) {
            return CommonResult.FAILURE;    // 사용자가 존재하지 않으면 실패
        }

        userEntity.setEmail(user.getEmail());
        userEntity.setName(user.getName());
        userEntity.setNickname(user.getNickname());
        userEntity.setPhone(user.getPhone());
        userEntity.setAddr(user.getAddr());
        userEntity.setBirthdate(user.getBirthdate());
        userEntity.setUpdatedAt(LocalDateTime.now());

        return this.userMapper.updateUser(userEntity) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public Result deleteUser(String email, String password) {
        if (email == null || email.isEmpty() ||
        password == null || password.isEmpty()) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectUserByEmail(email);
        if(dbUser == null) {        // 사용자가 존재하지 않는 경우
            return DeletedUserResult.USER_NOT_FOUND;
        }
        if(!email.equals(dbUser.getEmail())) {       // 클라이언트가 준 이메일과 DB에 있는 이메일이 틀리다면
            return DeletedUserResult.FAILURE_DUPLICATE_EMAIL;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(password, dbUser.getPassword())) {     // 클라이언트가 적은 패스워드와 DB에 있는 패스워드가 틀리다면
            return DeletedUserResult.FAILURE_DUPLICATE_PASSWORD;
        }

        dbUser.setDeletedDate(LocalDateTime.now());
        return this.userMapper.updateUser(dbUser) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
}
