package com.floristeria.blomst.service;

import com.floristeria.blomst.dto.user.User;
import com.floristeria.blomst.entity.user.CredentialEntity;
import com.floristeria.blomst.entity.user.RoleEntity;
import com.floristeria.blomst.enumeration.user.LoginType;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void createUser(String firstName, String lastName, String email, String password);

    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);

    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String email);

    CredentialEntity getUserCredentialById(Long id);

    User setUpMfa(Long id);

    User cancelMfa(Long id);

    User verifyQrCode(String userId, String qrCode);

    void createUserAdminWhenAppItsStartedForFirstTime();

    // methods update password,etc
    void resetPassword(String email);

    User verifyPasswordKey(String key);

    void updatePassword(String userId, String newPassword, String confirmedPassword);
    void updatePassword(String userId, String currentPassword,String newPassword, String confirmedPassword);

    User updateUser(String userId, String firstName, String lastName, String email, String bio);

    void updateRole(String userId, String role);

    void toggleAccountEnabled(String userId);

    void toggleAccountLocked(String userId);

    void toggleAccountExpired(String userId);

    String updatePhoto(String userId, MultipartFile file);
}
