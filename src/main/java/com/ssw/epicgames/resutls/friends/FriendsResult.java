package com.ssw.epicgames.resutls.friends;

import com.ssw.epicgames.resutls.Result;

public enum FriendsResult implements Result {
    FRIENDSHIP_EXISTS,      // 이미 친구 되어있음
    USER_NOT_FOUND   // 유저를 찾을 수 없음
}
