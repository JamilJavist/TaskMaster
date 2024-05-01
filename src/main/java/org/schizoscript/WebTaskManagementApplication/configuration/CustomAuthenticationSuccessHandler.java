package org.schizoscript.WebTaskManagementApplication.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Класс CustomAuthenticationSuccessHandler представляет собой пользовательский обработчик успешной аутентификации,
 * который наследуется от SimpleUrlAuthenticationSuccessHandler и переопределяет метод onAuthenticationSuccess.
 * После успешной аутентификации метод onAuthenticationSuccess извлекает пользователя из Authentication объекта,
 * получает идентификатор пользователя и формирует URL для перенаправления на страницу учетной записи пользователя.
 * Затем происходит перенаправление пользователя на сформированный URL.
 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request
            , HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        Long userId = user.getId();
        String targetUrl = "/account/" + userId;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
