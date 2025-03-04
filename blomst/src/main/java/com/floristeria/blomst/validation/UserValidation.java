package com.floristeria.blomst.validation;

import com.floristeria.blomst.entity.user.UserEntity;
import com.floristeria.blomst.exception.ApiException;

public class UserValidation {
    public static void verifyAccountStatus(UserEntity userEntity) {
        if (!userEntity.isEnabled()){throw new ApiException("Esta cuenta esta desactivada");}
        if (!userEntity.isAccountNonExpired()){throw new ApiException("Cuenta expirada");}
        if (!userEntity.isAccountNonLocked()){throw new ApiException("Esta cuenta esta bloqueada");}
    }
}
