package org.schizoscript.WebTaskManagementApplication.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Конфигурационный класс для настройки методов безопасности
 *
 * Аннотация @EnableGlobalMethodSecurity(prePostEnabled = true) распознает аннотации безопасности (такие как @PreAuthorize,
 *      @PostAuthorize, @PreFilter, @PostFilter) для методов и применяет соответствующие правила безопасности при вызове
 *      этих методов
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
}
