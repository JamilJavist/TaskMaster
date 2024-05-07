package org.schizoscript.WebTaskManagementApplication.dtos.factories;

import org.schizoscript.WebTaskManagementApplication.dtos.TaskDto;
import org.schizoscript.WebTaskManagementApplication.storage.entities.TaskEntity;
import org.springframework.stereotype.Component;

/**
 * Класс TaskDtoFactory служит в качестве фабрики для создания DTO объектов задач
 */
@Component
public class TaskDtoFactory {

    /**
     * Создает объект TaskDto на основе сущности TaskEntity
     *
     * @param entity сущность задачи
     * @return DTO объект задачи
     */
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
