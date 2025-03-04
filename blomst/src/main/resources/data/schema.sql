CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    user_id
    VARCHAR
(
    255
) NOT NULL,
    first_name VARCHAR
(
    50
) NOT NULL,
    last_name VARCHAR
(
    50
) NOT NULL,
    email VARCHAR
(
    100
) NOT NULL,
    phone VARCHAR
(
    30
) DEFAULT NULL,
    bio VARCHAR
(
    255
) DEFAULT NULL,
    reference_id VARCHAR
(
    255
) NOT NULL,
    qr_code_secret VARCHAR
(
    255
) DEFAULT NULL,
    qr_code_image_uri TEXT DEFAULT NULL,
    img_url VARCHAR
(
    255
) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    login_attempts INT DEFAULT 0,
    mfa BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN NOT NULL DEFAULT FALSE,
    account_non_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_users_email
(
    email
),
    UNIQUE KEY uq_users_user_id
(
    user_id
),
    CONSTRAINT fk_users_created_by FOREIGN KEY
(
    created_by
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE CASCADE,
    CONSTRAINT fk_users_updated_by FOREIGN KEY
(
    updated_by
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS confirmations
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    `key`
    VARCHAR
(
    255
) NOT NULL, -- Usamos comillas invertidas para evitar conflicto con la palabra reservada
    user_id BIGINT NOT NULL,
    reference_id VARCHAR
(
    255
) NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_confirmations_user_id
(
    user_id
),
    UNIQUE KEY uq_confirmations_key
(
    `key`
),
    CONSTRAINT fk_confirmations_user_id FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE CASCADE,
    CONSTRAINT fk_confirmations_created_by FOREIGN KEY
(
    created_by
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE CASCADE,
    CONSTRAINT fk_confirmations_updated_by FOREIGN KEY
(
    updated_by
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS credentials
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    password
    VARCHAR
(
    255
) NOT NULL,
    reference_id VARCHAR
(
    255
) NOT NULL,
    user_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_credentials_user_id
(
    user_id
),
    CONSTRAINT fk_credentials_user_id FOREIGN KEY
(
    user_id
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE CASCADE,
    CONSTRAINT fk_credentials_created_by FOREIGN KEY
(
    created_by
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE CASCADE,
    CONSTRAINT fk_credentials_updated_by FOREIGN KEY
(
    updated_by
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS roles
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    authorities
    VARCHAR
(
    255
) NOT NULL,
    name VARCHAR
(
    255
) NOT NULL,
    reference_id VARCHAR
(
    255
) NOT NULL,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_roles_created_by FOREIGN KEY
(
    created_by
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE RESTRICT,
    CONSTRAINT fk_roles_updated_by FOREIGN KEY
(
    updated_by
) REFERENCES users
(
    id
)
                                                   ON UPDATE CASCADE
                                                   ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS user_roles
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL,
    role_id
    BIGINT
    NOT
    NULL,
    CONSTRAINT
    fk_user_roles_user_id
    FOREIGN
    KEY
(
    user_id
) REFERENCES users
(
    id
) ON UPDATE CASCADE
  ON DELETE RESTRICT,
    CONSTRAINT fk_user_roles_role_id FOREIGN KEY
(
    role_id
) REFERENCES roles
(
    id
)
  ON UPDATE CASCADE
  ON DELETE RESTRICT
    );

CREATE INDEX index_users_email ON users (email);

CREATE INDEX index_users_user_id ON users (user_id);

CREATE INDEX index_confirmations_user_id ON confirmations (user_id);

CREATE INDEX index_credentials_user_id ON credentials (user_id);

CREATE INDEX index_user_roles_user_id ON user_roles (user_id);
