package com.workflow.failureawareworkflow.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflow.failureawareworkflow.dto.AIDiagnostic;
import com.workflow.failureawareworkflow.dto.FailureAnalysisResponse;
import com.workflow.failureawareworkflow.dto.StepResultEvent;
import com.workflow.failureawareworkflow.entity.FailureAnalysisEntity;
import com.workflow.failureawareworkflow.entity.WorkflowEntity;
import com.workflow.failureawareworkflow.entity.WorkflowStepEntity;
import com.workflow.failureawareworkflow.enums.FailureType;
import com.workflow.failureawareworkflow.repository.FailureAnalysisRepository;
import com.workflow.failureawareworkflow.service.AIAnalysisService;
import com.workflow.failureawareworkflow.service.FailureAnalyzerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class
FailureAnalyzerServiceImpl implements FailureAnalyzerService {

    private final FailureAnalysisRepository repository;
    private final AIAnalysisService aiAnalysisService;

    @Override
    public void analyze(WorkflowEntity workflow, WorkflowStepEntity step, StepResultEvent result){

        FailureAnalysisEntity analysis = buildAnalysis(
                        workflow,
                        step,
                        result);

        repository.save(analysis);
        String diagnostic = aiAnalysisService.generateDiagnostic(analysis, workflow, step);
        log.info("Ai diagnostic generated successfully");
        analysis.setAiDiagnostic(diagnostic);

        repository.save(analysis);

        System.out.println("Failure Analysis Generated");

    }

    @Override
    public FailureAnalysisResponse getFailureAnalysis(String workflowId){

        FailureAnalysisEntity entity = repository.findByWorkflowId(workflowId)
                        .orElseThrow();

        ObjectMapper mapper =
                new ObjectMapper();

        AIDiagnostic diagnostic = null;
        try{
            diagnostic = mapper.readValue(entity.getAiDiagnostic(), AIDiagnostic.class);
        }catch(JsonProcessingException e){
            System.err.println(
                    "Failed to parse AI diagnostic for workflow "
                            + workflowId
                            + " : "
                            + e.getMessage()
            );

            diagnostic = AIDiagnostic.builder()
                            .rootCause("AI diagnostic unavailable")
                            .operationalImpact("AI diagnostic unavailable")
                            .recoveryRecommendation("Inspect stored AI response")
                            .build();
        }


        return FailureAnalysisResponse
                .builder()
                .failureType(entity.getFailureType().name())
                .rootCause(entity.getRootCause())
                .impact(entity.getImpact())
                .stepName(entity.getStepName())
                .suggestedAction(entity.getSuggestedAction())
                .aiDiagnostic(diagnostic)
                .build();

    }

    private FailureAnalysisEntity
    buildAnalysis(WorkflowEntity workflow, WorkflowStepEntity step, StepResultEvent result){

        String rootCause;
        String impact;
        String action;
        FailureType failureType = result.getFailureType();

        if(failureType == null){
            failureType = FailureType.UNKNOWN;

        }

        switch(failureType){

            case TIMEOUT -> {

                rootCause = "Downstream service " + "did not respond " + "within retry window";

                impact = "Workflow execution " + "stopped before " + "completion";

                action = "Verify dependency " + "health and " + "retry workflow";

            }

            case VALIDATION_ERROR -> {

                rootCause = "Input validation " + "failed";

                impact = "Step execution " + "could not proceed";

                action = "Verify request " + "payload";

            }

            default -> {

                rootCause = "Unknown failure";

                impact = "Workflow halted";

                action = "Inspect logs";

            }

        }

        return FailureAnalysisEntity.builder()
                        .workflowId(workflow.getWorkflowId())
                        .stepName(step.getStepName())
                        .failureType(result.getFailureType())
                        .rootCause(rootCause)
                        .impact(impact)
                        .suggestedAction(action)
                        .createdAt(LocalDateTime.now())
                        .build();

    }

}