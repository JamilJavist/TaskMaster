package org.schizoscript.WebTaskManagementApplication.dtos.factories;

import org.schizoscript.WebTaskManagementApplication.dtos.ProjectDto;
import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.springframework.stereotype.Component;

/**
 * Класс ProjectDtoFactory служит в качестве фабрики для создания DTO объектов проектов
 */
@Component
public class ProjectDtoFactory {

    /**
     * Создает объект ProjectDto на основе сущности ProjectEntity
     *
     * @param entity сущность проекта
     * @return DTO объект проекта
     */
    public ProjectDto makeProjectDto(ProjectEntity entity) {
        return ProjectDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createAt(entity.getCreateAt())
                .build();
    }
}
