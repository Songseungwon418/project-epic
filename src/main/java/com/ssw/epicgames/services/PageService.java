package com.ssw.epicgames.services;

import com.ssw.epicgames.DTO.MyDTO;
import com.ssw.epicgames.entities.*;
import com.ssw.epicgames.mappers.*;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.resutls.friends.FriendsResult;
import com.ssw.epicgames.resutls.user.DeletedUserResult;
import com.ssw.epicgames.resutls.user.RegisterResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
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

    //regin 친구 추가
    public Result insertFriend(FriendsEntity friend) {
        if(friend == null ||
        friend.getUser_email() == null || friend.getUser_email().length() < 8 || friend.getUser_email().length() > 50 ||
        friend.getFriend_email() == null || friend.getFriend_email().length() < 8 || friend.getFriend_email().length() > 50) {
            return CommonResult.FAILURE;
        }

        FriendsEntity[] friendEntity = this.friendMapper.selectUserFriends(friend.getUser_email(), friend.getFriend_email());
                for(FriendsEntity friendList : friendEntity) {
                    if(friendList.getFriend_email().equals(friend.getFriend_email())) {
                        return FriendsResult.FRIENDSHIP_EXISTS;
                    }
                }

        if(this.userMapper.selectUserByEmail(friend.getFriend_email()) == null) {
            return FriendsResult.USER_NOT_FOUND;
        }

        friend.setUser_email(friend.getUser_email());
        friend.setFriend_email(friend.getFriend_email());
        friend.setCreatedAt(LocalDateTime.now());
        friend.setDeletedAt(null);

        return this.friendMapper.insertFriend(friend) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
    //endregion

    //region 친구 찾기
    public boolean isFriend(String userEmail, String friendEmail) {
        FriendsEntity[] friends = this.friendMapper.selectUserFriends(userEmail, friendEmail);
        for (FriendsEntity friend : friends) {
            if(friend.getFriend_email().equals(friendEmail)) {
                return true;
            }
        }
        return false;
    }
    //endregion

    //region 친구 찾기 배열
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
    //endregion

    //region 유저가 얻은 업적
    public MyDTO[] getUserPurchases(String email) {
        return this.achievementMapper.selectMyDTOs(email);
    }
    //endregion

    //region 유저 정보 수정
    public Result patchUser(UserEntity user) {
        if (user == null ||
                user.getEmail() == null || user.getEmail().length() < 8 || user.getEmail().length() > 50 ||
                user.getName() == null || user.getName().isEmpty() || user.getName().length() > 30 ||
                user.getNickname() == null || user.getNickname().length() < 2 || user.getNickname().length() > 10 ||
                user.getPhone() == null || user.getPhone().length() != 11) {
            return CommonResult.FAILURE;
        }

        UserEntity dbUser;
        if((dbUser = this.userMapper.selectUserByNickname(user.getNickname())) != null && !user.getEmail().equals(dbUser.getEmail())) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }

        if((dbUser = this.userMapper.selectUserByPhone(user.getPhone())) != null && !user.getEmail().equals(dbUser.getEmail())) {
            return RegisterResult.FAILURE_DUPLICATE_PHONE;
        }

        if (user.getBirthdate().isAfter(LocalDate.now())) {
            return RegisterResult.FAILURE_INVALID_DATE_FORMAT;
        }

        UserEntity userEntity = this.userMapper.selectUserByEmail(user.getEmail());
        if (userEntity == null) {
            return CommonResult.FAILURE;    // 사용자가 존재하지 않으면 실패
        }

        userEntity.setName(user.getName());
        userEntity.setNickname(user.getNickname());
        userEntity.setPhone(user.getPhone());
        userEntity.setPostcode(user.getPostcode());
        userEntity.setAddr(user.getAddr());
        userEntity.setDetailAddress(user.getDetailAddress());
        userEntity.setBirthdate(user.getBirthdate());
        userEntity.setUpdatedAt(LocalDateTime.now());

        return this.userMapper.updateUser(userEntity) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
    //endregion

    //region 계정 삭제
    public Result deleteUser(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return CommonResult.FAILURE;
        }

        UserEntity dbUser = this.userMapper.selectUserByEmail(email);
        if (dbUser == null) {
            return DeletedUserResult.USER_NOT_FOUND;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, dbUser.getPassword())) {
            return DeletedUserResult.FAILURE_DUPLICATE_PASSWORD;
        }

        // 탈퇴 처리
        dbUser.setDeletedDate(LocalDateTime.now());

        return this.userMapper.updateUser(dbUser) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }
    //endregion

    //region keyword 유저 검색
    public UserEntity[] getUserByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            keyword = "";
        }
        return this.userMapper.selectUserByKeyword(keyword);
    }
    //endregion
}
