package org.schizoscript.WebTaskManagementApplication.services;

import org.schizoscript.WebTaskManagementApplication.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.engine.ElementName;

@Service
@Transactional
public class AuxiliaryService {

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
