package org.schizoscript.WebTaskManagementApplication.storage.repositories;

import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Optional<ProjectEntity> findByName(String name);

    @Query("SELECT p FROM ProjectEntity p WHERE p.user.id = :userId")
    Stream<ProjectEntity> streamAllByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM ProjectEntity p WHERE p.user.id = :userId AND p.name LIKE CONCAT(:prefixName, '%')")
    Stream<ProjectEntity> streamAllByUserIdAndNameStartsWithPrefixName(@Param("userId") Long userId,
                                                                       @Param("prefixName") String prefixName);

    Optional<ProjectEntity> findProjectEntityByUserIdAndNameContainsIgnoreCase(Long userId, String projectName);

    Stream<ProjectEntity> streamAllBy();

    Stream<ProjectEntity> streamAllByNameStartsWithIgnoreCase(String prefixName);
}
