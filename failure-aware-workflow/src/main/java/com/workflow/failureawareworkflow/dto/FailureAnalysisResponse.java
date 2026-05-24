package com.workflow.failureawareworkflow.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FailureAnalysisResponse {
    private String stepName;
    private String failureType;
    private String rootCause;
    private String impact;
    private String suggestedAction;
    private AIDiagnostic aiDiagnostic;

}