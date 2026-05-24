package com.workflow.failureawareworkflow.entity;

import com.workflow.failureawareworkflow.enums.StepStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="workflow_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID stepId;

    private String stepName;

    private String topic;

    private Integer retryCount;
    private Integer executionOrder;

    private Integer maxRetries;

    @Enumerated(EnumType.STRING)
    private StepStatus status;

    @ManyToOne
    @JoinColumn(name="workflow_id")
    private WorkflowEntity workflow;
}
