package org.schizoscript.WebTaskManagementApplication.services;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.exceptions.NotFoundException;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.schizoscript.WebTaskManagementApplication.storage.entities.TaskEntity;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.ProjectRepository;
import org.schizoscript.WebTaskManagementApplication.storage.repositories.TaskRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuxiliaryService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    protected ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with \"%s\" doesn't exist.", projectId
                                )
                        )
                );
    }

    protected TaskEntity getTaskOrThrowException(Long taskId) {
        return taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Task state with \"%s\" id doesn't exist.",
                                        taskId
                                )
                        ));
    }
}
