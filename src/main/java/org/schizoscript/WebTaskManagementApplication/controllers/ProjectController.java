package org.schizoscript.WebTaskManagementApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.dtos.ProjectDto;
import org.schizoscript.WebTaskManagementApplication.dtos.factories.ProjectDtoFactory;
import org.schizoscript.WebTaskManagementApplication.services.ProjectService;
import org.schizoscript.WebTaskManagementApplication.services.UserService;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final UserService userService;
    private final ProjectService projectService;
    private final ProjectDtoFactory dtoFactory;

    @GetMapping("/account/{id}/projects")
    @PreAuthorize("#id == authentication.principal.id")
    public String getProjects(
            Model model,
            @PathVariable Long id,
            @RequestParam(name = "prefixName", required = false) Optional<String> prefixName) {
        List<ProjectDto> projectList = projectService.fetchProjectsByPrefixName(id, prefixName);

        model.addAttribute("projectList", projectList);

        return "projects";
    }

    @PostMapping("/account/{id}/projects/create")
    @PreAuthorize("#id == authentication.principal.id")
    public String createProject(@PathVariable Long id, @RequestParam(name = "projectName") String projectName) {
        ProjectDto projectDto = projectService.createProject(id, projectName);
        return "redirect:/account/{id}/projects";
    }

    @GetMapping("/account/{id}/projects/{projectId}")
    public String getProjectInfo(@PathVariable Long id, @PathVariable Long projectId, Model model, Principal principal) {

        UserEntity user = userService.findByEmail(principal.getName());

        ProjectEntity project = projectService.getProjectById(projectId);

        if (project != null && project.getUser().getId().equals(user.getId())) {
            ProjectDto projectDto = dtoFactory.makeProjectDto(project);

            model.addAttribute("project", project);
            return "project-info";
        } else {
            return "redirect:/error/access-denied";
        }
    }
}
