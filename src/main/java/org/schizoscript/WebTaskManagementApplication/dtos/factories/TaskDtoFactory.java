package org.schizoscript.WebTaskManagementApplication.dtos.factories;

import lombok.RequiredArgsConstructor;
import org.schizoscript.WebTaskManagementApplication.dtos.TaskDto;
import org.schizoscript.WebTaskManagementApplication.storage.entities.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {

    public TaskDto makeTaskDto(TaskEntity entity) {
        return TaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .leftTaskId(entity.getLeftTask().map(TaskEntity::getId).orElse(null))
                .rightTaskId(entity.getRightTask().map(TaskEntity::getId).orElse(null))
                .createAt(entity.getCreateAt())
                .build();
    }
}
