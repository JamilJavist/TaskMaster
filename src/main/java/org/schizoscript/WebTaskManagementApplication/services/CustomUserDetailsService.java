package org.schizoscript.WebTaskManagementApplication.services;

import org.schizoscript.WebTaskManagementApplication.storage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Класс CustomUserDetailsService реализует интерфейс UserDetailsService и предоставляет пользовательский сервис для
 *      загрузки информации о пользователе.
 * Метод loadUserByUsername() переопределяет метод интерфейса UserDetailsService для загрузки информации о пользователе по его email.
 * При вызове метода возвращается объект UserDetails, содержащий информацию о пользователе, найденного в репозитории по указанному email.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }
}
