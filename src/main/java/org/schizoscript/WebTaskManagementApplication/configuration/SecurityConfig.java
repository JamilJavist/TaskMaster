package org.schizoscript.WebTaskManagementApplication.configuration;

import org.schizoscript.WebTaskManagementApplication.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурационный класс для настройки безопасности веб-приложения
 */

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    /**
     * Конфигурация цепочки фильтров безопасности.
     * Здесь определяются правила доступа к URL, настройка формы входа, обработка исключений и выхода из системы.
     *
     * @param http объект HttpSecurity для настройки правил безопасности
     * @return SecurityFilterChain - цепочка фильтров безопасности
     * @throws Exception при возникновении ошибок во время конфигурации
     */
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/registration").permitAll()
                        .requestMatchers("/product/**", "/image/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll()
                )
                .exceptionHandling(
                        (exceptionHanding) -> exceptionHanding.accessDeniedPage("/error/access-denied")
                )
                .logout((logout) -> logout.permitAll());
        return http.build();
    }

    /**
     * Настройка AuthenticationManagerBuilder для установки пользовательского сервиса пользователей и шифрования паролей.
     *
     * @param auth объект AuthenticationManagerBuilder для настройки аутентификации
     * @throws Exception при возникновении ошибок во время конфигурации
     */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * Создание и конфигурация PasswordEncoder для шифрования паролей пользователей.
     *
     * @return PasswordEncoder - объект для шифрования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
