package org.schizoscript.WebTaskManagementApplication.storage.repositories;

import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    Optional<UserEntity> findById(Long id);
}
