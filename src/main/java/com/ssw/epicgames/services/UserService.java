package com.ssw.epicgames.services;

import com.ssw.epicgames.entities.EmailTokenEntity;
import com.ssw.epicgames.entities.UserEntity;
import com.ssw.epicgames.exceptions.TransactionalException;
import com.ssw.epicgames.mappers.EmailTokenMapper;
import com.ssw.epicgames.mappers.UserMapper;
import com.ssw.epicgames.resutls.CommonResult;
import com.ssw.epicgames.resutls.Result;
import com.ssw.epicgames.resutls.user.LoginResult;
import com.ssw.epicgames.resutls.user.RegisterResult;
import com.ssw.epicgames.resutls.user.ResolveRecoverPasswordResult;
import com.ssw.epicgames.resutls.user.ValidateEmailTokenResult;
import com.ssw.epicgames.utils.CryptoUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.transaction.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final EmailTokenMapper emailTokenMapper;
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public UserService(EmailTokenMapper emailTokenMapper, UserMapper userMapper, JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.emailTokenMapper = emailTokenMapper;
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public UserEntity getUserByEmail(String email) {
        return this.userMapper.selectUserByEmail(email);
    }

    //region 로그인
    public Result login(UserEntity user) {
        if(user == null ||
        user.getEmail() == null || user.getEmail().length() < 8 || user.getEmail().length() > 50 ||
        user.getPassword() == null || user.getPassword().length() < 6 || user.getPassword().length() > 50) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectUserByEmail(user.getEmail());
        if(dbUser == null || dbUser.getDeletedDate() != null) {
            return LoginResult.FAILURE_DELETED;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(user.getPassword(), dbUser.getPassword())) {
            return CommonResult.FAILURE;
        }
        if(!dbUser.isVerified()) {
            return LoginResult.FAILURE_NOT_VERIFIED;
        }
        user.setPassword(dbUser.getPassword());
        user.setName(dbUser.getName());
        user.setBirthdate(dbUser.getBirthdate());
        user.setNickname(dbUser.getNickname());
        user.setPhone(dbUser.getPhone());
        user.setAddr(dbUser.getAddr());
        user.setRegisterDate(dbUser.getRegisterDate());
        user.setDeletedDate(dbUser.getDeletedDate());
        user.setVerified(dbUser.isVerified());
        return CommonResult.SUCCESS;
    }
    //endregion

    //region 회원가입
    @Transactional
    public Result register(HttpServletRequest request, UserEntity user) throws MessagingException {
        if (user == null ||
                user.getEmail() == null || user.getEmail().length() < 8 || user.getEmail().length() > 50 ||
                user.getPassword() == null || user.getPassword().length() < 6 || user.getPassword().length() > 50 ||
                user.getName() == null || user.getName().isEmpty() || user.getName().length() > 30 ||
                user.getNickname() == null || user.getNickname().length() < 2 || user.getNickname().length() > 10 ||
                user.getBirthdate() == null ||
                user.getPhone() == null || user.getPhone().length() != 11) {
            return CommonResult.FAILURE;
        }
        // 사용자가 적은 이메일이 db에 있으면
        if(this.userMapper.selectUserByEmail(user.getEmail()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_EMAIL;
        }

        // 사용자가 적은 이메일이 이메일 양식과 다르면
        if(!user.getEmail().matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            return RegisterResult.FAILURE_NOT_EMAIL_FORMAT;
        }

        // 전화번호가 db에 있으면
        if(this.userMapper.selectUserByPhone(user.getPhone()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_PHONE;
        }

        //닉네임이 db에 있으면
        if(this.userMapper.selectUserByNickname(user.getNickname()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }

        // 생년월일이 미래인 경우
        if(user.getBirthdate().isAfter(LocalDate.now())) {
            return RegisterResult.FAILURE_INVALID_DATE_FORMAT;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setName(user.getName());
        user.setBirthdate(user.getBirthdate());
        user.setNickname(user.getNickname());
        user.setPhone(user.getPhone());
        user.setAddr(user.getAddr());
        user.setRegisterDate(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeletedDate(null);
        user.setVerified(false);
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
        emailToken.setUsed(false);
        if(this.emailTokenMapper.insertEmailToken(emailToken) == 0) {
            throw new TransactionException();
        }
        String validationLink = String.format("%s://%s:%d/user/validate-email-token?userEmail=%s&key=%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                emailToken.getUserEmail(),
                emailToken.getKey());
        Context context = new Context();
        context.setVariable("validationLink", validationLink);
        String mailText = this.templateEngine.process("email/registerEmail", context);
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("ttig0614@gamil.com");
        mimeMessageHelper.setTo(emailToken.getUserEmail());
        mimeMessageHelper.setSubject("[EPIC] 회원가입 인증 링크");
        mimeMessageHelper.setText(mailText, true);
        this.mailSender.send(mimeMessage);
        return CommonResult.SUCCESS;
    }
    //endregion

    //region 비밀번호 재설정 인증 링크 보내기
    @Transactional
    public Result provokeForgotPassword(HttpServletRequest request, String email) throws MessagingException {
        if(email == null || email.length() < 8 || email.length() > 50) {
            return CommonResult.FAILURE;
        }
        UserEntity user = this.userMapper.selectUserByEmail(email);
        if(user == null || user.getDeletedDate() != null) {
            return CommonResult.FAILURE;
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
        emailToken.setUsed(false);
        if(this.emailTokenMapper.insertEmailToken(emailToken) == 0) {
            throw new TransactionException();
        }
        String validationLink = String.format("%s://%s:%d/user/recover-password?userEmail=%s&key=%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                emailToken.getUserEmail(),
                emailToken.getKey());
        Context context = new Context();
        context.setVariable("validationLink", validationLink);
        String mailText = this.templateEngine.process("email/recoverPassword", context);
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("ttig0614@gamil.com");
        mimeMessageHelper.setTo(emailToken.getUserEmail());
        mimeMessageHelper.setSubject("[EPIC] 비밀번호 재설정 인증 링크");
        mimeMessageHelper.setText(mailText, true);
        this.mailSender.send(mimeMessage);

        return CommonResult.SUCCESS;
    }
    //endregion

    //region 비밀번호 재설정
    @Transactional
    public Result resolveRecoverPassword(EmailTokenEntity emailToken, String password) {
        if (emailToken == null ||
                emailToken.getUserEmail() == null || emailToken.getUserEmail().length() < 8 || emailToken.getUserEmail().length() > 50 ||
                emailToken.getKey() == null || emailToken.getKey().length() != 128 ||
                password == null || password.length() < 6 || password.length() > 50) {
            return CommonResult.FAILURE;
        }
        EmailTokenEntity dbEmailToken = this. emailTokenMapper.selectEmailTokenByUserEmailAndKey(emailToken.getUserEmail(), emailToken.getKey());
        if (dbEmailToken == null || dbEmailToken.isUsed()) {
            return CommonResult.FAILURE;
        }
        if(dbEmailToken.getExpiresAt().isBefore(LocalDateTime.now())) {     // 완료일이 지났다(현재시각이 지났다?)
            return ResolveRecoverPasswordResult.FAILURE_EXPIRED;
        }
        dbEmailToken.setUsed(true);
        if (this.emailTokenMapper.updateEmailToken(dbEmailToken) == 0) {
            throw new TransactionalException();
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserEntity user = this.userMapper.selectUserByEmail(emailToken.getUserEmail());
        user.setPassword(encoder.encode(password));     // 니가 사용할 신규 비밀번호
        if(this.userMapper.updateUser(user) == 0) {
            throw new TransactionalException();
        }
        return CommonResult.SUCCESS;
    }
    //endregion

    //region 이메일토큰
    @Transactional
    public Result validateEmailToken(EmailTokenEntity emailToken) {
        if(emailToken == null ||
        emailToken.getUserEmail() == null || emailToken.getUserEmail().length() < 8 || emailToken.getUserEmail().length() > 50 ||
        emailToken.getKey() == null || emailToken.getKey().length() != 128) {
            return CommonResult.FAILURE;
        }
        EmailTokenEntity dbEmailToken = this.emailTokenMapper.selectEmailTokenByUserEmailAndKey(emailToken.getUserEmail(), emailToken.getKey());
        if(dbEmailToken == null || dbEmailToken.isUsed()) {
            return CommonResult.FAILURE;
        }
        if (dbEmailToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ValidateEmailTokenResult.FAILURE_EXPIRED;
        }
        dbEmailToken.setUsed(true);
        if(this.emailTokenMapper.updateEmailToken(dbEmailToken) == 0) {
            throw new TransactionException();
        }
        UserEntity user = this.userMapper.selectUserByEmail(emailToken.getUserEmail());
        user.setVerified(true);
        if(this.userMapper.updateUser(user) == 0) {
            throw new TransactionException();
        }
        return CommonResult.SUCCESS;
    }
    //endregion
}
