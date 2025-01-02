package com.ssw.epicgames.interceptors;

import com.ssw.epicgames.entities.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {
    /** 인증된 유저로 로그인 했는지 확인 - 컨트롤러에 가기전에 작동하여 확인 후 컨트롤러로 넘어감 */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 현재 HTTP 요청에서 세션을 가져옵니다.
        HttpSession session = request.getSession();

        // 세션에서 "user" 객체를 가져옵니다.
        Object userObj = session.getAttribute("user");

        // 유저가 없거나, user 객체가 UserEntity 타입이 아닐 경우
        if (userObj == null || !(userObj instanceof UserEntity)) {
            // 로그인 페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/user/");
            return false; // 리다이렉트 후 더 이상 컨트롤러로 요청을 전달하지 않습니다.
        }

        // 세션에 "user" 객체가 UserEntity 타입이라면, 인증된 유저인지 확인
        UserEntity user = (UserEntity) userObj;

        // 인증되지 않은 유저일 경우
        if (!user.isVerified()) {
            // 홈페이지로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/");
            return false; // 리다이렉트 후 더 이상 컨트롤러로 요청을 전달하지 않습니다.
        }

        // 인증된 유저인 경우, 정상적으로 컨트롤러로 요청을 전달합니다.
        return true;
    }
}
