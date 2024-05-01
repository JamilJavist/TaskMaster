package org.schizoscript.WebTaskManagementApplication.controllers.errors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error/access-denied")
    public String accessDeniedError() {
        return "access-denied";
    }
}
