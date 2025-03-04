package com.floristeria.blomst.enumeration.user;


import lombok.Getter;

import static com.floristeria.blomst.constant.Constants.*;

@Getter
public enum Authority {
    USER(USER_AUTHORITIES),
    ADMIN(ADMIN_AUTHORITIES),
    SYS_ADMIN(SYS_ADMIN_AUTHORITIES);
    private final String value;

    Authority(String value) {
        this.value = value;
    }
}
