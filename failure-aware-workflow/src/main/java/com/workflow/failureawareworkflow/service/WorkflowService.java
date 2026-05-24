package com.workflow.failureawareworkflow.service;

import com.workflow.failureawareworkflow.dto.CreateWorkflowRequest;
import com.workflow.failureawareworkflow.dto.StepResultEvent;
import com.workflow.failureawareworkflow.dto.WorkflowStatusResponse;

import java.util.UUID;

public interface WorkflowService {

    UUID createWorkflow(CreateWorkflowRequest request);

    void executeWorkflow(UUID workflowId);
    void handleResult(StepResultEvent result);

    WorkflowStatusResponse getWorkflowStatus(UUID workflowId);
}
