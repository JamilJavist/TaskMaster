package org.schizoscript.WebTaskManagementApplication.dtos;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String description;

    @NonNull
    private Long ordinal;

    private Long leftTaskId;

    private Long rightTaskId;

    @NonNull
    private Instant createAt;
}
