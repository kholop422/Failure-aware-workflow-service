package com.workflow.failureawareworkflow.dto;

import com.workflow.failureawareworkflow.enums.WorkflowStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowCreateResponse {

    private String workflowId;

    private String workflowName;

    private WorkflowStatus status;

    private Integer totalSteps;

    private LocalDateTime createdAt;

}
