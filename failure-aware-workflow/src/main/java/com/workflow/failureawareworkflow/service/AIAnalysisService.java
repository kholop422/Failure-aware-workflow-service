package com.workflow.failureawareworkflow.service;

import com.workflow.failureawareworkflow.entity.FailureAnalysisEntity;
import com.workflow.failureawareworkflow.entity.WorkflowEntity;
import com.workflow.failureawareworkflow.entity.WorkflowStepEntity;

public interface AIAnalysisService {

    String generateDiagnostic(
            FailureAnalysisEntity entity,
            WorkflowEntity workflow,
            WorkflowStepEntity step
    );

}