package com.ssw.epicgames.mappers;

import com.ssw.epicgames.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    // 회원가입(user테이블에 insert)
    int insertUser(UserEntity user);

    // 동일 이메일 있는지 구분용
    UserEntity selectUserByEmail(@Param("email") String email);

    // 동일 휴대전화 번호 있는지 구분용
    UserEntity selectUserByPhone(@Param("phone") String phone);

    // 동일 닉네임이 있는지 구분용
    UserEntity selectUserByNickname(@Param("nickname") String nickname);

    int updateUser(UserEntity user);
}
