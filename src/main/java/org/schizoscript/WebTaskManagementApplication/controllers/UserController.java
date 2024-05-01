package org.schizoscript.WebTaskManagementApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.services.UserService;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String createUser(UserEntity user, Model model) {
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с " + user.getEmail() + " уже существует");
            return "registration";
        }
//        userService.createUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login/success")
    public String loginSuccess(Principal principal) {
        UserEntity user = userService.findByEmail(principal.getName());
        Long userId = user.getId();
        return "redirect:/account/" + userId;
    }
}
