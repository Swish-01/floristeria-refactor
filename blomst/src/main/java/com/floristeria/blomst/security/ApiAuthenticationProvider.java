package com.floristeria.blomst.security;


import com.floristeria.blomst.domain.ApiAuthentication;
import com.floristeria.blomst.domain.UserPrincipal;
import com.floristeria.blomst.exception.ApiException;
import com.floristeria.blomst.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.floristeria.blomst.constant.Constants.NINETY_DAYS;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var apiAuthentication = authenticationFunction.apply(authentication);
        var user = userService.getUserByEmail(apiAuthentication.getEmail());
        if (user != null) {
            var userCredential = userService.getUserCredentialById(user.getId());
            if (userCredential.getUpdatedAt().minusDays(NINETY_DAYS).isAfter(LocalDateTime.now())) {
                throw new ApiException("Credenciales expiradas. Por favor, cambié la contraseña");
            }
            //if(!user.isCredentialsNonExpired()) { throw new ApiException("Credentials are expired. Please reset your password"); }
            var userPrincipal = new UserPrincipal(user, userCredential);
            validAccount.accept(userPrincipal);
            if (encoder.matches(apiAuthentication.getPassword(), userCredential.getPassword())) {
                return ApiAuthentication.authenticated(user, userPrincipal.getAuthorities());
            } else throw new BadCredentialsException("Email/password incorrecto. Intentalo de nuevo");
        }
        throw new ApiException("No se ha podido autenticar el usuario");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

    private final Function<Authentication, ApiAuthentication> authenticationFunction = authentication -> (ApiAuthentication) authentication;

    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
        if (!userPrincipal.isAccountNonLocked()) {
            throw new LockedException("Tu cuenta esta bloqueada. Contacta con un administrador.");
        }
        if (!userPrincipal.isEnabled()) {
            throw new DisabledException("Tu cuenta esta desactivada. Contacta con un administrador.");
        }
        if (!userPrincipal.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Tu contraseña esta expirada. Por favor actualizala");
        }
        if (!userPrincipal.isAccountNonExpired()) {
            throw new DisabledException("Tu cuenta esta expirada. Contacta con un administrador.");
        }
    };
}
