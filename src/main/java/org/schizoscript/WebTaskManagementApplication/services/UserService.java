package org.schizoscript.WebTaskManagementApplication.services;

import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.schizoscript.WebTaskManagementApplication.storage.enums.Role;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Класс UserService предоставляет сервисные методы для работы с пользователями.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод createUser создает нового пользователя на основе переданного объекта user, шифрует пароль, добавляет к нему
     *      роль "ROLE_USER", сохраняет пользователя в базу и возвращает true.
     * @param user Переданный пользователь.
     * @return логическое значение, которое определяет сохранен ли новый пользователь или нет.
     */
    public boolean createUser(UserEntity user) {
        String email = user.getEmail();
        if (userRepository.findByEmail(email) != null) return false;
        user.setActive(true);
        user.getRoles().add(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Saving new user with email: {}", email);
        userRepository.save(user);
        return true;
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }
}
