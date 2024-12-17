package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.FriendsEntity;
import com.ssw.epicgames.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FriendMapper {
    FriendsEntity[] selectFriendsByUserEmail(@Param("user_email") String user_email);
}
