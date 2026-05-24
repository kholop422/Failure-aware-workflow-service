package com.workflow.failureawareworkflow.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepResultEvent {

    private UUID workflowId;

    private UUID stepId;

    private String stepName;

    private boolean success;

    private String errorMessage;

}
