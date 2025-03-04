package com.floristeria.blomst.service;

import com.floristeria.blomst.domain.Token;
import com.floristeria.blomst.domain.TokenData;
import com.floristeria.blomst.dto.user.User;
import com.floristeria.blomst.enumeration.user.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {
    String createToken(User user, Function<Token, String> tokenFunction);

    Optional<String> extractToken(HttpServletRequest request, String tokenType);

    void addCookie(HttpServletResponse response, User user, TokenType type);

    <T> T getTokenData(String token, Function<TokenData, T> tokenFunction);
    void removeCookieKey(HttpServletRequest request,HttpServletResponse response,String cookieName);
}
