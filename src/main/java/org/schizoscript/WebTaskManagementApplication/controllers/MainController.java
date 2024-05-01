package org.schizoscript.WebTaskManagementApplication.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Класс MainController представляет собой контроллер, отвечающий за обработку запросов к главной странице сайта
 */
@Controller
public class MainController {

    @GetMapping("/")
    public String main() {
        return "main";
    }
}
