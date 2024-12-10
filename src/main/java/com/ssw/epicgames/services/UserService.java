package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.EmailTokenEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.mappers.EmailTokenMapper;
import com.ssw.epicgames.mappers.UserMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.resutls.user.RegisterResult;
import com.ssw.epicgames.utils.CryptoUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.transaction.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final EmailTokenMapper emailTokenMapper;
    private final UserMapper userMapper;

    @Autowired
    public UserService(EmailTokenMapper emailTokenMapper, UserMapper userMapper) {
        this.emailTokenMapper = emailTokenMapper;
        this.userMapper = userMapper;
    }

    public Result register(UserEntity user) {
        if (user == null ||
                user.getEmail() == null || user.getEmail().length() < 8 || user.getEmail().length() > 50 ||
                user.getPassword() == null || user.getPassword().length() < 6 || user.getPassword().length() > 50 ||
                user.getName() == null || user.getName().isEmpty() || user.getName().length() > 30 ||
                user.getNickname() == null || user.getNickname().length() < 2 || user.getNickname().length() > 10 ||
                user.getPhone() == null || user.getPhone().length() != 11) {
            return CommonResult.FAILURE;
        }
        if(this.userMapper.selectUserByEmail(user.getEmail()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_EMAIL;
        }
        if(this.userMapper.selectUserByPhone(user.getPhone()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_PHONE;
        }
        if(this.userMapper.selectUserByNickname(user.getNickname()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setName(user.getName());
        user.setBirthdate(user.getBirthdate());
        user.setNickname(user.getNickname());
        user.setPhone(user.getPhone());
        user.setAddr(null);
        user.setRegisterDate(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeletedDate(null);
        user.setIsVerified(0);
        if(this.userMapper.insertUser(user) == 0) {
            throw new TransactionException();
        }
        EmailTokenEntity emailToken = new EmailTokenEntity();
        emailToken.setUserEmail(user.getEmail());
        emailToken.setKey(CryptoUtils.hashSha512(String.format("%s%s%f%f",
                user.getEmail(),
                user.getPassword(),
                Math.random(),
                Math.random())));
        emailToken.setCreatedAt(LocalDateTime.now());
        emailToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        emailToken.setIsUsed(false);
        if(this.emailTokenMapper.insertEmailToken(emailToken) == 0) {
            throw new TransactionException();
        }
        return CommonResult.SUCCESS;
    }
}
