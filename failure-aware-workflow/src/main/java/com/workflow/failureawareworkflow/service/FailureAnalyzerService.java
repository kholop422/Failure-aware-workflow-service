package com.workflow.failureawareworkflow.service;

import com.workflow.failureawareworkflow.dto.FailureAnalysisResponse;
import com.workflow.failureawareworkflow.dto.StepResultEvent;
import com.workflow.failureawareworkflow.entity.WorkflowEntity;
import com.workflow.failureawareworkflow.entity.WorkflowStepEntity;

import java.util.UUID;

public interface FailureAnalyzerService {

    void analyze(
            WorkflowEntity workflow,
            WorkflowStepEntity step,
            StepResultEvent result
    );

    FailureAnalysisResponse getFailureAnalysis(String workflowId);

}