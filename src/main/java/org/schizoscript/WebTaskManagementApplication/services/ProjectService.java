package org.schizoscript.WebTaskManagementApplication.services;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.dtos.AckDto;
import org.schizoscript.WebTaskManagementApplication.dtos.ProjectDto;
import org.schizoscript.WebTaskManagementApplication.dtos.factories.ProjectDtoFactory;
import org.schizoscript.WebTaskManagementApplication.exceptions.BadRequestException;
import org.schizoscript.WebTaskManagementApplication.exceptions.NotFoundException;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.schizoscript.WebTaskManagementApplication.storage.entities.UserEntity;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.ProjectRepository;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

    private final AuxiliaryService auxService;
    private final ProjectDtoFactory dtoFactory;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

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

    public ProjectEntity getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseGet(null);
    }

    private ProjectDto editProject(Long userId, Long projectId, String projectName) {

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

    private AckDto deleteProject(Long userId, Long projectId) {
        ProjectEntity project = auxService.getEntityOrThrowException(projectRepository, userId, "Project");

        projectRepository.deleteById(projectId);

        return AckDto.makeDefault(true);
    }
}
