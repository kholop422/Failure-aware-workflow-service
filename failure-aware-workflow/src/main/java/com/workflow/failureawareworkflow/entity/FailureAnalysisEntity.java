package com.workflow.failureawareworkflow.entity;

import com.workflow.failureawareworkflow.enums.FailureType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="failure_analysis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID analysisId;

    private String workflowId;

    private String stepName;

    @Enumerated(EnumType.STRING)
    private FailureType failureType;

    @Column(length = 3000)
    private String rootCause;

    @Column(length = 3000)
    private String impact;

    @Column(length = 3000)
    private String suggestedAction;

    @Column(length=5000)
    private String aiDiagnostic;

    private LocalDateTime createdAt;

}
