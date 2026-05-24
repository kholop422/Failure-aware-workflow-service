package com.workflow.failureawareworkflow.dto;

import com.workflow.failureawareworkflow.enums.WorkflowStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStatusResponse {

    private UUID workflowId;

    private String workflowName;

    private WorkflowStatus status;

    private Integer currentStep;

}
