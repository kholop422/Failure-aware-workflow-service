package com.workflow.failureawareworkflow.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecutionEvent {

    private String workflowId;

    private UUID stepId;

    private String stepName;

}
