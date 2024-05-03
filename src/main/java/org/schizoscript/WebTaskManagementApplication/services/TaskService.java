package org.schizoscript.WebTaskManagementApplication.services;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.dtos.AckDto;
import org.schizoscript.WebTaskManagementApplication.dtos.TaskDto;
import org.schizoscript.WebTaskManagementApplication.dtos.factories.TaskDtoFactory;
import org.schizoscript.WebTaskManagementApplication.exceptions.BadRequestException;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.schizoscript.WebTaskManagementApplication.storage.entities.TaskEntity;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;
    private final TaskDtoFactory dtoFactory;
    private final AuxiliaryService auxService;

    private List<TaskDto> getTasks(Long projectId) {

        ProjectEntity project = auxService.getProjectOrThrowException(projectId);

        return project
                .getTaskList()
                .stream()
                .map(dtoFactory::makeTaskDto)
                .collect(Collectors.toList());
    }

    private TaskDto createTask(Long projectId, String taskName) {

        if (taskName.trim().isEmpty()) {
            throw new BadRequestException("Task name can't be empty.");
        }

        ProjectEntity project = auxService.getProjectOrThrowException(projectId);

        Optional<TaskEntity> optionalAnotherTask = Optional.empty();

        for (TaskEntity task : project.getTaskList()) {
            if (task.getName().equalsIgnoreCase(taskName)) {
                throw new BadRequestException(String.format("Task state \"%s\" already exists.", taskName));
            }

            if (!task.getRightTask().isPresent()) {
                optionalAnotherTask = Optional.of(task);
                break;
            }
        }

        TaskEntity task = repository.saveAndFlush(
                TaskEntity.builder()
                        .name(taskName)
                        .project(project)
                        .build()
        );

        optionalAnotherTask
                .ifPresent(anotherTask -> {
                    task.setLeftTask(anotherTask);
                    anotherTask.setRightTask(task);
                    repository.saveAndFlush(anotherTask);
                });

        final TaskEntity savedTask = repository.saveAndFlush(task);

        return dtoFactory.makeTaskDto(savedTask);
    }

    private TaskDto updateTask(Long taskId, String taskName) {

        if (taskName.trim().isEmpty()) {
            throw new BadRequestException("Task state name can't be empty.");
        }

        TaskEntity task = auxService.getTaskOrThrowException(taskId);

        repository
                .findTaskByProjectIdAndNameContainsIgnoreCase(
                        task.getProject().getId(),
                        taskName
                )
                .filter(anotherTask -> !anotherTask.getId().equals(taskId))
                .ifPresent(anotherTask -> {
                    throw new BadRequestException(String.format("Task state \"%s\" already exists.", taskName));
                });

        task.setName(taskName);

        task = repository.saveAndFlush(task);

        return dtoFactory.makeTaskDto(task);
    }

    private TaskDto changeTaskPosition(Long taskId, Optional<Long> optionalLeftTaskId) {

        TaskEntity changeTask = auxService.getTaskOrThrowException(taskId);

        ProjectEntity project = changeTask.getProject();

        Optional<Long> optionalOldLeftTaskId = changeTask
                .getLeftTask()
                .map(TaskEntity::getId);

        if (optionalOldLeftTaskId.equals(optionalLeftTaskId)) {
            return dtoFactory.makeTaskDto(changeTask);
        }

        Optional<TaskEntity> optionalNewLeftTask = optionalLeftTaskId
                .map(leftTaskId -> {
                    if (taskId.equals(leftTaskId)) {
                        throw new BadRequestException("Left task state id equals changed task state.");
                    }

                    TaskEntity leftTaskEntity = auxService.getTaskOrThrowException(leftTaskId);

                    if (!project.getId().equals(leftTaskEntity.getProject().getId())) {
                        throw new BadRequestException("Task state position can be changed within the same project.");
                    }

                    return leftTaskEntity;
                });

        Optional<TaskEntity> optionalNewRightTask;
        if (!optionalNewLeftTask.isPresent()) {
            optionalNewRightTask = project
                    .getTaskList()
                    .stream()
                    .filter(anotherTask -> !anotherTask.getLeftTask().isPresent())
                    .findAny();
        } else {
            optionalNewRightTask = optionalNewLeftTask.get().getRightTask();
        }

        replaceOldTaskStatePosition(changeTask);

        if (optionalNewLeftTask.isPresent()) {
            TaskEntity newLeftTask = optionalNewLeftTask.get();

            newLeftTask.setRightTask(changeTask);

            changeTask.setRightTask(newLeftTask);
        } else {
            changeTask.setLeftTask(null);
        }

        if (optionalNewRightTask.isPresent()) {
            TaskEntity newRightTask = optionalNewRightTask.get();

            newRightTask.setLeftTask(changeTask);

            changeTask.setRightTask(newRightTask);
        } else {
            changeTask.setRightTask(null);
        }

        changeTask = repository.saveAndFlush(changeTask);

        optionalNewLeftTask
                .ifPresent(repository::saveAndFlush);

        optionalNewRightTask
                .ifPresent(repository::saveAndFlush);

        return dtoFactory.makeTaskDto(changeTask);
    }

    private AckDto deleteTask(Long taskId) {

        TaskEntity changeTask = auxService.getTaskOrThrowException(taskId);

        replaceOldTaskStatePosition(changeTask);

        repository.delete(changeTask);

        return AckDto.builder().answer(true).build();
    }

    private void replaceOldTaskStatePosition(TaskEntity changeTask) {
        Optional<TaskEntity> optionalOldLeftTask = changeTask.getLeftTask();
        Optional<TaskEntity> optionalOldRightTask = changeTask.getRightTask();

        optionalOldLeftTask
                .ifPresent(it -> {
                    it.setRightTask(optionalOldRightTask.orElse(null));

                    repository.saveAndFlush(it);
                });

        optionalOldRightTask
                .ifPresent(it -> {
                    it.setLeftTask(optionalOldLeftTask.orElse(null));

                    repository.saveAndFlush(it);
                });
    }
}
