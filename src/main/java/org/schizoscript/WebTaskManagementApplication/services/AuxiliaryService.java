package org.schizoscript.WebTaskManagementApplication.services;

import org.schizoscript.WebTaskManagementApplication.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.engine.ElementName;

/**
 * Класс AuxiliaryService представляет собой сервисный класс, предоставляющий вспомагательные методы
 */
@Service
@Transactional
public class AuxiliaryService {

    /**
     * Получает сущность из репозитория по её идентификатору или выбрасывает исключение, еслм сущность не найдена
     *
     * @param <T>        тип сущности
     * @param repository репозиторий для получения сущности
     * @param entityId   идентификатор сущности
     * @param entityName название сущности
     * @return найденная сущность или исключение, если сущность не найдена
     */
    public <T> T getEntityOrThrowException(JpaRepository<T, Long> repository, Long entityId, String entityName) {

        return repository.findById(entityId)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format(
                                        "%s with id \"%s\" doesn't exist.",
                                        entityName,
                                        entityId
                                )
                        )
                );
    }
}
