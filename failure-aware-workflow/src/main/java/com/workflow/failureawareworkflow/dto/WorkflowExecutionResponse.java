package com.workflow.failureawareworkflow.dto;

import com.workflow.failureawareworkflow.enums.WorkflowStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecutionResponse {

    private String workflowId;

    private WorkflowStatus status;

    private String currentStep;

    private String message;

}
