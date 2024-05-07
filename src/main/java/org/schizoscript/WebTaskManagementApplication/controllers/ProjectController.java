package org.schizoscript.WebTaskManagementApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.schizoscript.WebTaskManagementApplication.dtos.ProjectDto;
import org.schizoscript.WebTaskManagementApplication.dtos.TaskDto;
import org.schizoscript.WebTaskManagementApplication.dtos.factories.ProjectDtoFactory;
import org.schizoscript.WebTaskManagementApplication.services.ProjectService;
import org.schizoscript.WebTaskManagementApplication.services.TaskService;
import org.schizoscript.WebTaskManagementApplication.services.UserService;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Класс ProjectController предназначен для обработки запросов связанных с сущностью ProjectEntity
 */
@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final TaskService taskService;
    private final UserService userService;
    private final ProjectDtoFactory dtoFactory;
    private final ProjectService projectService;

    /**
     * Обработка GET запроса для получения всех проектов либо проектов по префиксу имени
     *
     * @param model модель для передачи данных в представление
     * @param id идентификатор пользователя
     * @param prefixName префикс имени проекта
     * @return имя представления
     */
    @GetMapping("/account/{id}/projects")
    @PreAuthorize("#id == authentication.principal.id")
    public String getProjects(
            Model model,
            @PathVariable Long id,
            @RequestParam(name = "prefixName", required = false) Optional<String> prefixName
    ) {

        List<ProjectDto> projectList = projectService.fetchProjectsByPrefixName(id, prefixName);

        model.addAttribute("projectList", projectList);

        return "projects";
    }

    /**
     * Обработка POST запроса для создания проекта
     *
     * @param id идентификатор пользователя
     * @param projectName название проекта, который будет сохранен
     * @return имя представления
     */
    @PostMapping("/account/{id}/projects/create")
    @PreAuthorize("#id == authentication.principal.id")
    public String createProject(@PathVariable Long id, @RequestParam(name = "projectName") String projectName) {

        ProjectDto projectDto = projectService.createProject(id, projectName);

        return "redirect:/account/{id}/projects";
    }

    /**
     * Обработка GET запроса для получения детальной информации об проекте
     * <p>
     * Для защиты от несанционированного доступа к странице, получаем пользователя через principal. Затем получаем
     *      проект через его ID, а затем проверяем userId проекта с ID пользователя полученного через principal. В случае
     *      успеха, создаем projectDto и список задач проекта tasksList и добавляем в модель для отображения на странице.
     *      В случае неудачи, мы перенаправляем на страницу с сообщением об ошибке.
     *
     * @param id идентификатор пользователя
     * @param projectId идентификатор проекта
     * @param model модель для передачи данных на представление
     * @param principal объект представляющий текущего пользователя
     * @return имя представления
     */
    @GetMapping("/account/{id}/projects/{projectId}")
    public String getProjectInfo(
            @PathVariable Long id, @PathVariable Long projectId, Model model, Principal principal
    ) {

        UserEntity user = userService.findByEmail(principal.getName());

        ProjectEntity project = projectService.getProjectById(projectId);

        if (project != null && project.getUser().getId().equals(user.getId())) {
            ProjectDto projectDto = dtoFactory.makeProjectDto(project);
            List<TaskDto> tasksList = taskService.getTasksList(projectId);

            model.addAttribute("project", projectDto);
            model.addAttribute("tasks", tasksList);
            
            return "project-info";
        } else {
            return "redirect:/error/access-denied";
        }
    }

    /**
     * Обработка POST запроса для редактирования информации об проекте
     * <p>
     * Для защиты от несанционированного доступа к странице, получаем пользователя через principal. Затем получаем
     *      проект через его ID, а затем проверяем userId проекта с ID пользователя полученного через principal. В случае
     *      успеха, изменяем информацию об проекте и сохраняем изменения в базу данных. В случае неудачи, мы перенаправляем
     *      на страницу с сообщением об ошибке.
     *
     * @param id идентификатор пользователя
     * @param projectId идентификатор проекта
     * @param newProjectName новое название проекта
     * @param principal объект представляющий текущего пользователя
     * @return имя представления
     */
    @PostMapping("/account/{id}/projects/{projectId}")
    public String updateProject(
            @PathVariable Long id, @PathVariable Long projectId,
            @RequestParam(name = "projectName") String newProjectName, Principal principal
    ) {

        UserEntity user = userService.findByEmail(principal.getName());

        ProjectEntity project = projectService.getProjectById(projectId);

        if (project != null && project.getUser().getId().equals(user.getId())) {
            projectService.editProject(id, projectId, newProjectName);

            return "redirect:/account/{id}/projects/{projectId}";
        } else {
            return "redirect:/error/access-denied";
        }
    }

    /**
     * Обработка POST запроса для удалении проекта
     * <p>
     * Для защиты от несанционированного доступа к странице, получаем пользователя через principal. Затем получаем
     *      проект через его ID, а затем проверяем userId проекта с ID пользователя полученного через principal. В случае
     *      успеха, удаляем сущность проекта в базе данных. В случае неудачи, мы перенаправляем на страницу с сообщением
     *      об ошибке.
     *
     * @param id идентификатор пользователя
     * @param projectId идентификатор проекта
     * @param principal объект представляющий текущего пользователя
     * @return имя представления
     */
    @PostMapping("/account/{id}/projects/{projectId}/delete")
    public String deleteProject(@PathVariable Long id, @PathVariable Long projectId, Principal principal) {

        UserEntity user = userService.findByEmail(principal.getName());

        ProjectEntity project = projectService.getProjectById(projectId);

        if (project != null && project.getUser().getId().equals(user.getId())) {
            projectService.deleteProject(id, projectId);

            return "redirect:/account/{id}/projects";
        } else {
            return "redirect:/error/access-denied";
        }
    }
}
