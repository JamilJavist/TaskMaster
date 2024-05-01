package org.schizoscript.WebTaskManagementApplication.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
