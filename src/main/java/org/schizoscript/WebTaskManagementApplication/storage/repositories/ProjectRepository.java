package org.schizoscript.WebTaskManagementApplication.storage.repositories;

import org.schizoscript.WebTaskManagementApplication.storage.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.stream.Stream;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Optional<ProjectEntity> findByName(String name);

    Stream<ProjectEntity> streamAllBy();

    Stream<ProjectEntity> streamAllByNameStartsWithIgnoreCase(String prefixName);

    @Query("SELECT p FROM project p WHERE p.user_id = :userId")
    Stream<ProjectEntity> findProjectByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM project p WHERE p.user_id = :userId AND p.name LIKE CONCAT(:prefixName, '%')")
    Stream<ProjectEntity> findProjectByUserIdAndNameStartingWith(@Param("userId") Long userId,
                                                                 @Param("prefixName") String prefixName);
}
