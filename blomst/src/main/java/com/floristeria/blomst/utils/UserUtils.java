package com.floristeria.blomst.utils;

import com.floristeria.blomst.dto.user.User;
import com.floristeria.blomst.entity.user.CredentialEntity;
import com.floristeria.blomst.entity.user.RoleEntity;
import com.floristeria.blomst.entity.user.UserEntity;
import com.floristeria.blomst.exception.ApiException;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import org.springframework.beans.BeanUtils;

import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.floristeria.blomst.constant.Constants.AFRICAN_SAFARI;
import static com.floristeria.blomst.constant.Constants.NINETY_DAYS;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class UserUtils {
    public static UserEntity createUserEntity(String firstName, String lastName, String email, RoleEntity roleEntity){
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .email(email.trim().toLowerCase())
                .lastLogin(now())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .mfa(false)
                .enabled(false)
                .loginAttempts(0)
                .qrCodeSecret(EMPTY)
                .phone(EMPTY)
                .bio(EMPTY)
                .imgUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png")
                .roles(Set.of(roleEntity))
                .build();
    }

    public static User fromUserEntity(UserEntity userEntity, Set<RoleEntity> roles, CredentialEntity credentialEntity) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        user.setLastLogin(userEntity.getLastLogin() != null ? userEntity.getLastLogin().toString() : null);
        user.setCredentialsNonExpired(isCredentialsNonExpired(credentialEntity));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());

        String roleNames = roles.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.joining(", "));
        String authorities = roles.stream()
                .map(RoleEntity::getAuthorities)
                .collect(Collectors.joining(", "));

        user.setRole(roleNames);
        user.setAuthorities(authorities);
        return user;
    }


    public static boolean isCredentialsNonExpired(CredentialEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(NINETY_DAYS).isAfter(now());
    }
    public static BiFunction<String, String, QrData> qrDataFunction = (email, qrCodeSecret) -> new QrData.Builder()
            .issuer(AFRICAN_SAFARI)
            .label(email)
            .secret(qrCodeSecret)
            .algorithm(HashingAlgorithm.SHA1)
            .digits(6)
            .period(30)
            .build();

    public static BiFunction<String, String, String> qrCodeImageUri = (email, qrCodeSecret) -> {
        var data = qrDataFunction.apply(email, qrCodeSecret);
        var generator = new ZxingPngQrGenerator();
        byte[] imageData;
        try {
            imageData = generator.generate(data);
        } catch (Exception exception) {
            //throw new ApiException(exception.getMessage());
            throw new ApiException("Unable to create QR code URI");
        }
        return getDataUriForImage(imageData, generator.getImageMimeType());

    };

    public static Supplier<String> qrCodeSecret = () -> new DefaultSecretGenerator().generate();
}
