SET FOREIGN_KEY_CHECKS = 0;
INSERT INTO users (id, user_id, first_name, last_name, email, phone, bio,
                   reference_id, image_url, created_by, updated_by, created_at, updated_at, account_non_expired, account_non_locked, enabled, mfa)
VALUES (0, 'system-user-id', 'System', 'System', 'system@gmail.com', '1234567890', 'This is the system user itself',
        '00000000-0000-0000-0000-000000000000', 'https://cdn-icons-png.flaticon.com/128/2911/2911833.png', 1, 1, '2024-01-29 22:10:47.725642', '2024-01-29 22:10:47.725642', TRUE, TRUE, FALSE, FALSE);
SET FOREIGN_KEY_CHECKS = 1;
-- Inserting ROLE_USER
INSERT INTO ROLES (id, created_at, created_by, reference_id, updated_at, updated_by, authorities, name)
VALUES (1, NOW(), 1, '3f8e09f0-1234-4abc-8d9e-0123456789ab', NOW(), 1,
        'document:create,document:read,document:update,document:delete', 'USER');

-- Inserting ROLE_ADMIN
INSERT INTO ROLES (id, created_at, created_by, reference_id, updated_at, updated_by, authorities, name)
VALUES (2, NOW(), 1, '1a7b29c5-5678-4def-a123-4567890abcde', NOW(), 1,
        'user:create,user:read,user:update,document:create,document:read,document:update,document:delete', 'ADMIN');

-- Inserting ROLE_SUPER_ADMIN
INSERT INTO ROLES (id, created_at, created_by, reference_id, updated_at, updated_by, authorities, name)
VALUES (3, NOW(), 1, 'de5c7b9a-9abc-4bde-8123-abcdef987654', NOW(), 1,
        'user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete', 'SUPER_ADMIN');

-- Inserting ROLE_MANAGER
INSERT INTO ROLES (id, created_at, created_by, reference_id, updated_at, updated_by, authorities, name)
VALUES (4, NOW(), 1, 'b39f2c3d-4567-4f12-a9bc-1234567890ff', NOW(), 1,
        'document:create,document:read,document:update,document:delete', 'MANAGER');
