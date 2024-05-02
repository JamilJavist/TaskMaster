package org.schizoscript.WebTaskManagementApplication.storage.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Optional;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String description;

    @OneToOne
    private TaskEntity leftTask;

    @OneToOne
    private TaskEntity rightTask;

    @ManyToOne
    private ProjectEntity project;

    @Builder.Default
    @Column(name = "createAt")
    private Instant createAt = Instant.now();


    public Optional<TaskEntity> getLeftTask() {
        return Optional.ofNullable(leftTask);
    }

    public Optional<TaskEntity> getRightTask() {
        return Optional.ofNullable(rightTask);
    }
}
