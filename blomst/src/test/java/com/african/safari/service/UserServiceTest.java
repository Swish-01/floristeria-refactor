package com.african.safari.service;

import com.floristeria.blomst.entity.user.CredentialEntity;
import com.floristeria.blomst.entity.user.RoleEntity;
import com.floristeria.blomst.entity.user.UserEntity;
import com.floristeria.blomst.enumeration.user.Authority;
import com.floristeria.blomst.repository.user.CredentialRepository;
import com.floristeria.blomst.repository.user.UserRepository;
import com.floristeria.blomst.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialRepository credentialRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserEntity createUserEntity() {
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserId("1");
        userEntity.setFirstName("Barack Obama");
        userEntity.setEmail("Obama@gmail.com");
        userEntity.setCreatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setLastLogin(LocalDateTime.of(1990, 11, 1, 1, 11, 11));

        var roleEntity = new RoleEntity("USER", Authority.USER.toString());
        userEntity.setRoles(Set.of(roleEntity));

        return userEntity;
    }

    private CredentialEntity createCredentialEntity(UserEntity userEntity) {
        var credentialEntity = new CredentialEntity();
        credentialEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        credentialEntity.setPassword("IHateTrump");
        credentialEntity.setUserEntity(userEntity);

        return credentialEntity;
    }

    @Test
    @DisplayName("Test Find User By Id")
    public void getUserByUserIdTest() {
        // Arrange - Given
        var userEntity = createUserEntity();
        var credentialEntity = createCredentialEntity(userEntity);

        when(userRepository.findUserByUserId("1")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));

        // Act - When
        var userByUserId = userServiceImpl.getUserByUserId("1");

        // Assert - Then
        assertThat(userByUserId.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userByUserId.getUserId()).isEqualTo(userEntity.getUserId());
    }

    @Test
    @DisplayName("Test Find User By Email")
    public void getUserByEmailTest() {
        // Arrange - Given
        var userEntity = createUserEntity();
        var credentialEntity = createCredentialEntity(userEntity);

        when(userRepository.findByEmailIgnoreCase("Obama@gmail.com")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));

        // Act - When
        var userByEmail = userServiceImpl.getUserByEmail("Obama@gmail.com");

        // Assert - Then
        assertThat(userByEmail.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userByEmail.getUserId()).isEqualTo(userEntity.getUserId());
    }
}
