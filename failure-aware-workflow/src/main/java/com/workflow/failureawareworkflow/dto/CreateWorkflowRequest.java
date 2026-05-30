package com.workflow.failureawareworkflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateWorkflowRequest {

    private String workflowName;

    private List<WorkflowStepDTO> steps;
}