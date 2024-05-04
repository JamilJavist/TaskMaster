package org.schizoscript.WebTaskManagementApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.dtos.ProjectDto;
import org.schizoscript.WebTaskManagementApplication.services.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;

    @GetMapping("/account/{id}/projects")
    public String getProjects(
            Model model,
            @PathVariable Long id,
            @RequestParam(name = "prefixName", required = false) Optional<String> prefixName) {
        List<ProjectDto> projectList = service.fetchProjectsByPrefixName(id, prefixName);

        model.addAttribute("projectList", projectList);

        return "projects";
    }

    @PostMapping("/account/{id}/projects/create")
    public String createProject(@PathVariable Long id, @RequestParam(name = "projectName") String projectName) {
        ProjectDto projectDto = service.createProject(id, projectName);
        return "redirect:/account/{id}/projects";
    }
}
