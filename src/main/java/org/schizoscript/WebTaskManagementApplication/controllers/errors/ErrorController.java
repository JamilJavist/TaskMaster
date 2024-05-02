package org.schizoscript.WebTaskManagementApplication.controllers.errors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Класс ErrorController представляет собой контроллер, отвечающий за обработку ошибок и исключений в приложении
 */
@Controller
public class ErrorController {
    private final String ACCESS_DENIED_TEMPLATE_PATH = "errors/access-denied";

    /**
     * Метод, обрабатывающий запрос на генерацию страницы с сообщением об отказе в доступе.
     * @return Строка, указывающая на необходимость отображения страницы с сообщением об отказе в доступе.
     */
    @GetMapping("/error/access-denied")
    public String accessDeniedError() {
        return ACCESS_DENIED_TEMPLATE_PATH;
    }
}
