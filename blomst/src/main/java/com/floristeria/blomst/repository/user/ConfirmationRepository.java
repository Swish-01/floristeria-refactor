package com.floristeria.blomst.repository.user;

import com.floristeria.blomst.entity.user.ConfirmationEntity;
import com.floristeria.blomst.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, Long> {
    Optional<ConfirmationEntity> findByKey(String key);

    Optional<ConfirmationEntity> findByUserEntity(UserEntity userEntity);
}
