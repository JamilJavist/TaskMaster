package org.schizoscript.WebTaskManagementApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.services.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PreAuthorize("#id == authentication.principal.id")
    @PostMapping("/account/{id}/projects/{projectId}/create_task")
    public String createTaskInProject(
            @PathVariable Long id, @PathVariable Long projectId,
            @RequestParam(name = "taskName") String taskName,
            @RequestParam(name = "taskDescription") String taskDescription
    ) {

        service.createTask(projectId, taskName, taskDescription);
        return "redirect:/account/{id}/projects/{projectId}";
    }
}
