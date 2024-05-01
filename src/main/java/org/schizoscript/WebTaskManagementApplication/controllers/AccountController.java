package org.schizoscript.WebTaskManagementApplication.controllers;

import org.schizoscript.WebTaskManagementApplication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Класс AccountController представляет собой контроллер, отвечающий за вход в пользовательский аккаунт после аутентификации
 */
@Controller
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    /**
     * Метод getAccountPage предназначен для обработки запроса на отображение страницы учетной записи пользователя. По пути
     *      "/account/{id}" метод получает идентификатор пользователя из URL, а также модель для передачи данных на страницу
     *
     * Перед отображением страницы метод проверяет соответствие индентификатора пользователя текущему пользователю, используя
     *      аннотацию @PreAuthorize с выражением "#id == authentication.principal.id".
     * Если пользователь с заданным идентификатором найден в системе, его данные добавляются в модель для отображения на
     *      странице "account". В случае, если пользователя не удалось найти, выбрасывается исключение IllegalArgumentException
     *      с сообщением "User not found".
     * @param id Идентификатор пользователя, чья учетная запись должна отобразиться на странице.
     * @param model Модель, через которую передаются данные на страницу.
     * @return Наименование представления , которое должно использоваться для отображения страницы учетной записи.
     */
    @GetMapping("/account/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public String getAccountPage(@PathVariable Long id, Model model) {
        UserEntity user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("email", user.getEmail());
        model.addAttribute("id", user.getId());

        return "account";
    }
}
