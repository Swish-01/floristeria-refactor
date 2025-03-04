package com.floristeria.blomst.handler;

import com.floristeria.blomst.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import static com.floristeria.blomst.enumeration.user.TokenType.ACCESS;
import static com.floristeria.blomst.enumeration.user.TokenType.REFRESH;

@Service
@RequiredArgsConstructor
public class ApiLogoutOutHandler implements LogoutHandler {
    private final JwtService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
        jwtService.removeCookieKey(request, response, ACCESS.getValue());
        jwtService.removeCookieKey(request, response, REFRESH.getValue());
    }
}
