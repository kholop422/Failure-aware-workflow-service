package com.workflow.failureawareworkflow.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIDiagnostic {

    private String rootCause;

    private String operationalImpact;

    private String recoveryRecommendation;

}
