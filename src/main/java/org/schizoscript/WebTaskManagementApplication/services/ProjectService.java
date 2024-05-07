package org.schizoscript.WebTaskManagementApplication.services;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.dtos.AckDto;
import org.schizoscript.WebTaskManagementApplication.dtos.ProjectDto;
import org.schizoscript.WebTaskManagementApplication.dtos.factories.ProjectDtoFactory;
import org.schizoscript.WebTaskManagementApplication.exceptions.BadRequestException;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.ProjectRepository;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс ProjectService представляет собой сервисный класс
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final AuxiliaryService auxService;
    private final ProjectDtoFactory dtoFactory;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    /**
     * Метод fetchProjectsByPrefixName предназначен для получения всех проектов, либо проектов по префиксу.
     *
     * @param userId идентификатор пользователя
     * @param optionalPrefixName опциональный префикс для поиска проектов
     * @return список DTO объектов проектов
     */
    public List<ProjectDto> fetchProjectsByPrefixName(Long userId, Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectsStream;

        if (optionalPrefixName.isPresent()) {
            projectsStream = projectRepository.streamAllByUserIdAndNameStartsWithPrefixName(userId, optionalPrefixName.get());
        } else {
            projectsStream = projectRepository.streamAllByUserId(userId);
        }

        return projectsStream
                .map(dtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод createProject предназначен для создания проекта
     *
     * @param userId идентификатор пользователя
     * @param projectName имя проекта, который будет создан
     * @return объект DTO проекта
     */
    public ProjectDto createProject(Long userId, String projectName) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestException("Project name can't be empty");
        }

        UserEntity user = auxService.getEntityOrThrowException(userRepository, userId, "User");

        for (ProjectEntity project : user.getProjectList()) {
            if (project.getName().equalsIgnoreCase(projectName)) {
                throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
            }
        }

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(projectName)
                        .user(user)
                        .build()
        );

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return dtoFactory.makeProjectDto(project);
    }

    /**
     * Метод getProjectById предназначен для получения проекта по его идентификатору
     *
     * @param projectId идентификатор проекта
     * @return сущность проекта
     */
    public ProjectEntity getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseGet(null);
    }

    /**
     * Метод editProject предназначен для редактирования информации об проекте
     *
     * @param userId идентификатор пользователя
     * @param projectId идентификатор проекта
     * @param projectName новое имя проекта
     * @return объект DTO проекта
     */
    public ProjectDto editProject(Long userId, Long projectId, String projectName) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestException("Project name can't be empty");
        }

        UserEntity user = auxService.getEntityOrThrowException(userRepository, userId, "User");

        ProjectEntity project = auxService.getEntityOrThrowException(projectRepository, projectId, "Project");

        projectRepository
                .findProjectEntityByUserIdAndNameContainsIgnoreCase(project.getUser().getId(), projectName)
                .filter(anotherProject -> !anotherProject.getId().equals(projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
                });

        project.setName(projectName);

        project = projectRepository.saveAndFlush(project);

        return dtoFactory.makeProjectDto(project);
    }

    /**
     * Метод deleteProject предназначен для удаления проекта по идентификатору
     *
     * @param userId идентификатор пользователя
     * @param projectId идентификатор проекта
     */
    public void deleteProject(Long userId, Long projectId) {
        ProjectEntity project = auxService.getEntityOrThrowException(projectRepository, userId, "Project");

        projectRepository.deleteById(projectId);

//        return AckDto.makeDefault(true);
    }
}
