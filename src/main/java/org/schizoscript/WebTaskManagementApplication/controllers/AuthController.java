package org.schizoscript.WebTaskManagementApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.services.UserService;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

/**
 * Класс AuthController представляет собой контроллер, отвечающий за обработку запросов для аутентификации и авторизации
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    /**
     * Метод createUser предназначен для обработки запроса на создание нового пользователя
     *
     * @param user Сущность пользователя, которая будет сохранена
     * @param model Модель, через которую будет передаваться информация об ошибке
     * @return Наименование представления , которое будет переадресовывать на страницу аутентификации
     */
    @PostMapping("/registration")
    public String createUser(UserEntity user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с " + user.getEmail() + " уже существует");
            return "registration";
        }
        return "redirect:/login";
    }

    /**
     * Метод loginSuccess обрабатывает успешный запрос на успешный вход пользователя в систему и перенаправляет его на
     *      страницу учетной записи.
     * При вызове метода по пути "/login/success" извлекается объект Principal, который представляет текущего
     *      аутентифицированного пользователя.
     * Далее, используя сервис userService, извлекается пользователь с помощью email, полученного из Principal.
     * Полученный пользователь используется для извлечения идентификатора пользователя (userId), который затем
     *      добавляется к пути "/account/" для перенаправления.
     * Наконец, метод возвращает строку "redirect:/account/" + userId, чтобы перенаправить пользователя на страницу
     *      учетной записи соответствующего пользователя.
     */
    @GetMapping("/login/success")
    public String loginSuccess(Principal principal) {
        UserEntity user = userService.findByEmail(principal.getName());
        Long userId = user.getId();
        return "redirect:/account/" + userId;
    }
}
