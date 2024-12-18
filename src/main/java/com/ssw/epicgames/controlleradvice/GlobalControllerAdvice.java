package com.ssw.epicgames.controlleradvice;


import com.ssw.epicgames.entities.UserEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("isLoggedIn")
    public boolean addIsLoggedIn(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        return user != null;
    }

    @ModelAttribute("userNickname")
    public String addUserNickname(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        return user != null ? user.getNickname() : null;
    }

    @ModelAttribute("userEmail")
    public String addUserEmail(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        return user != null ? user.getEmail() : null;
    }
}
