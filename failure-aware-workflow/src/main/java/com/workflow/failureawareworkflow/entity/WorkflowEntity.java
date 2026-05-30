package com.workflow.failureawareworkflow.entity;

import com.workflow.failureawareworkflow.enums.WorkflowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="workflow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowEntity {

    @Id
    private String workflowId;

    private String workflowName;

    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;

    private Integer currentStepIndex;

    private LocalDateTime createdAt;


    @OneToMany(
            mappedBy = "workflow",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("executionOrder ASC")
    private List<WorkflowStepEntity> steps;
}
