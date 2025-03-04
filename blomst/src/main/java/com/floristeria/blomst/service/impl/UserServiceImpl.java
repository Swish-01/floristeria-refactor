package com.floristeria.blomst.service.impl;

import com.floristeria.blomst.cache.CacheStore;
import com.floristeria.blomst.domain.RequestContext;
import com.floristeria.blomst.dto.user.User;
import com.floristeria.blomst.entity.user.ConfirmationEntity;
import com.floristeria.blomst.entity.user.CredentialEntity;
import com.floristeria.blomst.entity.user.RoleEntity;
import com.floristeria.blomst.entity.user.UserEntity;
import com.floristeria.blomst.enumeration.user.Authority;
import com.floristeria.blomst.enumeration.user.EventType;
import com.floristeria.blomst.enumeration.user.LoginType;
import com.floristeria.blomst.event.UserEvent;
import com.floristeria.blomst.exception.ApiException;
import com.floristeria.blomst.repository.user.ConfirmationRepository;
import com.floristeria.blomst.repository.user.CredentialRepository;
import com.floristeria.blomst.repository.user.RoleRepository;
import com.floristeria.blomst.repository.user.UserRepository;
import com.floristeria.blomst.service.UserService;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import static com.floristeria.blomst.constant.Constants.PHOTO_DIRECTORY;
import static com.floristeria.blomst.utils.UserUtils.*;
import static com.floristeria.blomst.validation.UserValidation.verifyAccountStatus;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;


@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ConfirmationRepository confirmationRepository;
    private final BCryptPasswordEncoder encoder;
    private final CacheStore<String, Integer> cacheStore;
    private final ApplicationEventPublisher publisher;

    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email));
        var credentialEntity = new CredentialEntity(userEntity, encoder.encode(password));
        credentialRepository.save(credentialEntity);
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        publisher.publishEvent(new UserEvent(userEntity, EventType.REGISTRATION, Map.of("key", confirmationEntity.getKey())));
    }

    @Override
    public void createUserAdminWhenAppItsStartedForFirstTime() {
        RoleEntity SUPER_ADMIN = roleRepository.findByNameIgnoreCase("SUPER_ADMIN").orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setName("SUPER_ADMIN");
            role.setAuthorities("all:all");
            log.info("ROL SUPER_ADMIN CREADO.");
            return roleRepository.save(role);
        });

        roleRepository.findByNameIgnoreCase("USER").orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setName("USER");
            role.setAuthorities("document:create,document:read,document:update,document:delete");
            log.info("ROL USER CREADO");
            return roleRepository.save(role);
        });

        userRepository.findByEmailIgnoreCase("admin@admin.com").ifPresentOrElse(
                admin -> System.out.println("Usuario admin ya existe."),
                () -> {
                    // Primero creamos y guardamos el adminUser en la base de datos
                    UserEntity adminUser = createAdminUserEntity(SUPER_ADMIN);
                    adminUser = userRepository.save(adminUser); // Guardar el adminUser antes de crear CredentialEntity

                    // Ahora creamos CredentialEntity con el usuario guardado
                    var credentialEntity = new CredentialEntity(adminUser, encoder.encode("password"));
                    credentialRepository.save(credentialEntity);

                    log.info("Usuario admin creado con éxito.");
                }
        );
    }

    @Override
    public void resetPassword(String email) {
        var user = getUserEntityByEmail(email);
        var confirmation = getUserConfirmation(user);
        if (confirmation != null) {
            //enviar confirmación existente
            publisher.publishEvent(new UserEvent(user, EventType.RESET_PASSWORD, Map.of("key", confirmation.getKey())));
        } else {
            var confirmationEntity = new ConfirmationEntity(user);
            confirmationRepository.save(confirmationEntity);
            publisher.publishEvent(new UserEvent(user, EventType.RESET_PASSWORD, Map.of("key", confirmationEntity.getKey())));
        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        if (confirmationEntity == null) {
            throw new ApiException("No se ha encontrado el token");
        }
        var userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        if (userEntity == null) {
            throw new ApiException("Token incorrecto");
        }
        verifyAccountStatus(userEntity);
        confirmationRepository.delete(confirmationEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updatePassword(String userId, String newPassword, String confirmedPassword) {
        if (!confirmedPassword.equals(newPassword)) {
            throw new ApiException("Las contraseñas no coinciden. Por favor intentalo de nuevo.");
        }
        var user = getUserEntityByEmail(userId);
        var credentials = getUserCredentialById(user.getId());
        credentials.setPassword(encoder.encode(newPassword));
        credentialRepository.save(credentials);
    }

    @Override
    public void updatePassword(String userId, String currentPassword, String newPassword, String confirmedPassword) {
        if (!confirmedPassword.equals(newPassword)) {
            throw new ApiException("Las contraseñas nuevas no coinciden. Por favor intentalo de nuevo.");
        }
        var user = getUserEntityByUserId(userId);
        verifyAccountStatus(user);
        var credentials = getUserCredentialById(user.getId());
        if (!encoder.matches(currentPassword, credentials.getPassword())) {
            throw new ApiException("Las contraseñas actual no coinciden. Por favor intentalo de nuevo.");
        }
        credentials.setPassword(encoder.encode(newPassword));
        credentialRepository.save(credentials);
    }

    @Override
    public User updateUser(String userId, String firstName, String lastName, String email, String bio) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setBio(bio);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public void updateRole(String userId, String role) {
        var userEntity = getUserEntityByUserId(userId);
        var roleEntity = getRoleName(role);
        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleEntity);
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountExpired(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonExpired(!userEntity.isAccountNonExpired());
        userRepository.save(userEntity);
    }

    @Override
    public String updatePhoto(String userId, MultipartFile file) {
        var userEntity = getUserEntityByUserId(userId);
        var photoUrl = photoFunction.apply(userId, file);
        userEntity.setImgUrl(photoUrl + "?timestamp=" + System.currentTimeMillis());
        userRepository.save(userEntity);
        return photoUrl;
    }

    //cambiar este método para subirlo a un bucket
    private final BiFunction<String, MultipartFile, String> photoFunction = (id, file) -> {
        var fileName = id + ".png";
        try {
            var fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
                Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);
            }
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("user/image/" + fileName).toUriString();
        } catch (Exception exception) {
            throw new ApiException("No se ha podido guardar la imagen");
        }
    };

    @Override
    public void toggleAccountLocked(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setAccountNonLocked(!userEntity.isAccountNonLocked());
        userRepository.save(userEntity);
    }

    @Override
    public void toggleAccountEnabled(String userId) {
        var userEntity = getUserEntityByUserId(userId);
        userEntity.setEnabled(!userEntity.isEnabled());
        userRepository.save(userEntity);
    }

    @Override
    public RoleEntity getRoleName(String name) {
        var role = roleRepository.findByNameIgnoreCase(name);
        log.info("ROL{}", role.toString());
        return role.orElseThrow(() -> new ApiException("Rol no encontrado"));
    }

    @Override
    public void verifyAccountKey(String key) {
        var confirmationEntity = getUserConfirmation(key);
        UserEntity userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);
        RequestContext.setUserId(userEntity.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (cacheStore.get(userEntity.getEmail()) == null) {
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);
                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                cacheStore.put(userEntity.getEmail(), userEntity.getLoginAttempts());
                if (cacheStore.get(userEntity.getEmail()) > 5)
                    userEntity.setAccountNonLocked(false);
            }
            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);

                userEntity.setLastLogin(LocalDateTime.now());
                cacheStore.evict(userEntity.getEmail());
            }
        }
        userRepository.save(userEntity);
    }

    @Override
    public User getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findUserByUserId(userId).orElseThrow(() -> new ApiException("Usuario no encontrado"));
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        var credentialEntity = credentialRepository.getCredentialByUserEntityId(userId);
        return credentialEntity.orElseThrow(() -> new ApiException("Credentiales no encontradas "));
    }

    @Override
    public User setUpMfa(Long id) {
        var userEntity = getUserEntityById(id);
        var codeSecret = qrCodeSecret.get();
        userEntity.setQrCodeImageUri(qrCodeImageUri.apply(userEntity.getEmail(), codeSecret));
        userEntity.setQrCodeSecret(codeSecret);
        userEntity.setMfa(true);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User cancelMfa(Long id) {
        var userEntity = getUserEntityById(id);
        userEntity.setMfa(false);
        userEntity.setQrCodeSecret(EMPTY);
        userEntity.setQrCodeImageUri(EMPTY);
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User verifyQrCode(String userId, String qrCode) {
        var userEntity = getUserEntityByUserId(userId);
        verifyCode(qrCode, userEntity.getQrCodeSecret());
        return fromUserEntity(userEntity, userEntity.getRoles(), getUserCredentialById(userEntity.getId()));
    }

    private boolean verifyCode(String qrCode, String qrCodeSecret) {
        if (qrCodeSecret == null) {
            throw new ApiException("QR Code secret not configured for user");
        }
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (codeVerifier.isValidCode(qrCodeSecret, qrCode)) {
            return true;
        } else {
            throw new ApiException("Invalid QR code. Please try again.");
        }
    }


    private UserEntity getUserEntityByUserId(String userId) {
        var userByUserId = userRepository.findUserByUserId(userId);
        return userByUserId.orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityById(Long id) {
        var userById = userRepository.findById(id);
        return userById.orElseThrow(() -> new ApiException("User not found"));
    }

    private UserEntity getUserEntityByEmail(String email) {
        var userByEmail = userRepository.findByEmailIgnoreCase(email);
        return userByEmail.orElseThrow(() -> new ApiException("Usuario no encontrado"));
    }

    private ConfirmationEntity getUserConfirmation(String key) {
        return confirmationRepository.findByKey(key).orElseThrow(() -> new ApiException("Clave de confirmación no encontrada"));
    }

    private ConfirmationEntity getUserConfirmation(UserEntity user) {
        return confirmationRepository.findByUserEntity(user).orElse(null);
    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Authority.USER.name());
        return createUserEntity(firstName, lastName, email, role);
    }

    protected UserEntity createAdminUserEntity(RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName("ADMIN")
                .lastName("ADMIN")
                .email("admin@admin.com")
                .phone("+1234567890")
                .roles(Set.of(role))
                .mfa(false)
                .loginAttempts(0)
                .bio("Cuenta administradora del sistema")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .lastLogin(LocalDateTime.now())
                .enabled(true)
                .build();
    }
}