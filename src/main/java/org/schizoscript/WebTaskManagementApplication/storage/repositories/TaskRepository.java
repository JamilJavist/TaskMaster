package org.schizoscript.WebTaskManagementApplication.storage.repositories;

import org.schizoscript.WebTaskManagementApplication.storage.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findTaskByProjectIdAndNameContainsIgnoreCase(Long projectId, String taskName);
}
