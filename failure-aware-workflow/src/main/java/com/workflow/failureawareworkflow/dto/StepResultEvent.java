package com.workflow.failureawareworkflow.dto;

import com.workflow.failureawareworkflow.enums.FailureType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepResultEvent {

    private String workflowId;

    private UUID stepId;

    private String stepName;

    private boolean success;

    private String errorMessage;
    private FailureType failureType;

}
