package com.workflow.failureawareworkflow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowStepDTO {

    private String stepName;

    private String topic;

    private Integer maxRetries;
}
