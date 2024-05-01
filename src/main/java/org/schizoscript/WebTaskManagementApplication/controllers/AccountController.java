package org.schizoscript.WebTaskManagementApplication.controllers;

import org.schizoscript.WebTaskManagementApplication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @GetMapping("/account/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public String getAccountPage(@PathVariable Long id, Model model) {
        UserEntity user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("email", user.getEmail());
        model.addAttribute("id", user.getId());

        return "account";
    }
}
