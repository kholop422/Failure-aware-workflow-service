package com.workflow.failureawareworkflow.service;

import com.workflow.failureawareworkflow.dto.*;

import java.util.UUID;

public interface WorkflowService {

    WorkflowCreateResponse createWorkflow(CreateWorkflowRequest request);

    WorkflowExecutionResponse executeWorkflow(String workflowId);
    void handleResult(StepResultEvent result);

    WorkflowStatusResponse getWorkflowStatus(String workflowId);

    FailureAnalysisResponse getFailureAnalysis(String workflowId);
}
