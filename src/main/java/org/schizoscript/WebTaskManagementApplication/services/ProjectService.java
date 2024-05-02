package org.schizoscript.WebTaskManagementApplication.services;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.dtos.AckDto;
import org.schizoscript.WebTaskManagementApplication.dtos.ProjectDto;
import org.schizoscript.WebTaskManagementApplication.dtos.factories.ProjectDtoFactory;
import org.schizoscript.WebTaskManagementApplication.exceptions.BadRequestException;
import org.schizoscript.WebTaskManagementApplication.exceptions.NotFoundException;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.ProjectRepository;
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

    private final ProjectRepository repository;
    private final ProjectDtoFactory dtoFactory;

    private List<ProjectDto> fetchProjectsByPrefixName(Optional<String> optionalPrefixName) {
        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectsStream = optionalPrefixName
                .map(repository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(repository::streamAllBy);

        return projectsStream
                .map(dtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    private ProjectDto createProject(String projectName) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestException("Project name can't be empty");
        }

        repository
                .findByName(projectName)
                .ifPresent(
                        project -> {
                            throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
                });

        ProjectEntity project = repository.saveAndFlush(
                ProjectEntity.builder()
                        .name(projectName)
                        .build()
        );

        return dtoFactory.makeProjectDto(project);
    }

    private ProjectDto editProject(Long projectId, String projectName) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestException("Project name can't be empty");
        }

        ProjectEntity project = repository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with \"%s\" doesn't exists.",
                                        projectName
                                )
                        )
                );

        repository
                .findByName(projectName)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
                });


        project.setName(projectName);

        project = repository.saveAndFlush(project);

        return dtoFactory.makeProjectDto(project);
    }

    private AckDto deleteProject(Long projectId) {
        getProjectOrThrowException(projectId);

        repository.deleteById(projectId);

        return AckDto.makeDefault(true);
    }

    private ProjectEntity getProjectOrThrowException(Long projectId) {
        return repository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with \"%s\" doesn't exist.", projectId
                                )
                        )
                );
    }
}
