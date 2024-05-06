package org.schizoscript.WebTaskManagementApplication.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.dtos.TaskDto;
import org.schizoscript.WebTaskManagementApplication.dtos.factories.TaskDtoFactory;
import org.schizoscript.WebTaskManagementApplication.exceptions.BadRequestException;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.schizoscript.WebTaskManagementApplication.storage.entities.TaskEntity;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.ProjectRepository;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskDtoFactory dtoFactory;
    private final TaskRepository taskRepository;
    private final AuxiliaryService auxiliaryService;
    private final ProjectRepository projectRepository;

    public List<TaskDto> getTasksList(Long projectId) {

        ProjectEntity project = auxiliaryService.getEntityOrThrowException(projectRepository, projectId, "Project");

        return project
                .getTaskList()
                .stream()
                .map(dtoFactory::makeTaskDto)
                .collect(Collectors.toList());
    }

    public TaskDto createTask(Long projectId, String taskName, String taskDescription) {

        if (taskName.trim().isEmpty() || taskDescription.trim().isEmpty()) {
            throw new BadRequestException("Task name or description can't be empty");
        }

        ProjectEntity project = auxiliaryService.getEntityOrThrowException(projectRepository, projectId, "Project");

        Optional<TaskEntity> optionalAnotherTask = Optional.empty();

        for (TaskEntity taskFromLoop : project.getTaskList()) {
            if (taskFromLoop.getName().equalsIgnoreCase(taskName)) {
                throw new BadRequestException(String.format("Task state \"%s\" already exists.", taskName));
            }

            if (!taskFromLoop.getRightTask().isPresent()) {
                optionalAnotherTask = Optional.of(taskFromLoop);
                break;
            }
        }

        TaskEntity task = taskRepository.saveAndFlush(
                TaskEntity.builder()
                        .name(taskName)
                        .description(taskDescription)
                        .project(project)
                        .build()
        );

        optionalAnotherTask
                .ifPresent(anotherTask -> {
                    task.setLeftTask(anotherTask);
                    anotherTask.setRightTask(task);
                    taskRepository.saveAndFlush(anotherTask);
                });

        final TaskEntity savedTask = taskRepository.saveAndFlush(task);

        return dtoFactory.makeTaskDto(savedTask);
    }
}
