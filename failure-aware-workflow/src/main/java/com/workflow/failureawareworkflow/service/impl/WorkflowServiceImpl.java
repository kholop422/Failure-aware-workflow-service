package com.workflow.failureawareworkflow.service.impl;

import com.workflow.failureawareworkflow.dto.*;
import com.workflow.failureawareworkflow.entity.FailureAnalysisEntity;
import com.workflow.failureawareworkflow.entity.WorkflowEntity;
import com.workflow.failureawareworkflow.entity.WorkflowStepEntity;
import com.workflow.failureawareworkflow.enums.StepStatus;
import com.workflow.failureawareworkflow.enums.WorkflowStatus;
import com.workflow.failureawareworkflow.exception.WorkflowNotFoundException;
import com.workflow.failureawareworkflow.repository.FailureAnalysisRepository;
import com.workflow.failureawareworkflow.repository.WorkflowRepository;
import com.workflow.failureawareworkflow.service.FailureAnalyzerService;
import com.workflow.failureawareworkflow.service.WorkflowKafkaProducer;
import com.workflow.failureawareworkflow.service.WorkflowService;
import com.workflow.failureawareworkflow.utils.WorkflowIdGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowKafkaProducer kafkaProducer;
    private final FailureAnalyzerService failureAnalyzer;
    private final FailureAnalysisRepository failureAnalysisRepository;

    @Override
    public WorkflowCreateResponse createWorkflow(CreateWorkflowRequest request) {

        WorkflowEntity workflow = WorkflowEntity.builder()
                        .workflowId(WorkflowIdGenerator.generate())
                        .workflowName(request.getWorkflowName())
                        .status(WorkflowStatus.CREATED)
                        .currentStepIndex(0)
                        .createdAt(LocalDateTime.now())
                        .build();

        List<WorkflowStepEntity> steps =
                IntStream.range(0, request.getSteps().size())
                        .mapToObj(index -> {

                            WorkflowStepDTO dto =
                                    request.getSteps().get(index);

                            return WorkflowStepEntity
                                    .builder()
                                    .stepName(dto.getStepName())
                                    .topic(dto.getTopic())
                                    .executionOrder(index)
                                    .retryCount(0)
                                    .maxRetries(dto.getMaxRetries())
                                    .status(StepStatus.PENDING)
                                    .workflow(workflow)
                                    .build();

                        }).toList();

        workflow.setSteps(steps);

        WorkflowEntity saved = workflowRepository.save(workflow);
        log.info("Workflow created successfully with workflowId {}", saved.getWorkflowId());
        return WorkflowCreateResponse
                .builder()
                .workflowId(saved.getWorkflowId())
                .workflowName(saved.getWorkflowName())
                .status(saved.getStatus())
                .totalSteps(saved.getSteps().size())
                .createdAt(saved.getCreatedAt())
                .build();

    }

    @Override
    public WorkflowExecutionResponse executeWorkflow(String workflowId) {

        WorkflowEntity workflow = workflowRepository.findById(workflowId)
                        .orElseThrow(() -> new WorkflowNotFoundException(workflowId));

        workflow.setStatus(WorkflowStatus.RUNNING);

        startCurrentStep(workflow);

        workflowRepository.save(workflow);

        return WorkflowExecutionResponse.builder()
                .workflowId(workflow.getWorkflowId())
                .status(workflow.getStatus())
                .currentStep(workflow.getSteps()
                                .get(workflow.getCurrentStepIndex())
                                .getStepName())
                .message("Workflow execution initiated")
                .build();

    }

    @Override
    public void handleResult(StepResultEvent result){

        WorkflowEntity workflow = workflowRepository
                        .findById(result.getWorkflowId())
                        .orElseThrow();

        WorkflowStepEntity step = workflow.getSteps()
                        .get(workflow.getCurrentStepIndex());

        if(result.isSuccess()){
            System.out.println("Step SUCCESS : " + step.getStepName());

            step.setStatus(StepStatus.SUCCESS);

            workflow.setCurrentStepIndex(workflow.getCurrentStepIndex()+1);

            startCurrentStep(workflow);
        }else {
            handleFailure(workflow, step, result);

        }
        workflowRepository.save(workflow);

    }

    @Override
    public WorkflowStatusResponse getWorkflowStatus(String workflowId){

        WorkflowEntity workflow = workflowRepository
                        .findById(workflowId)
                        .orElseThrow(() ->
                                        new WorkflowNotFoundException(workflowId));
        String currentStepName = null;

        if(workflow.getStatus() == WorkflowStatus.SUCCESS){
            currentStepName = "COMPLETED";
        }
        else if (workflow.getCurrentStepIndex() < workflow.getSteps().size()) {
            currentStepName = workflow.getSteps()
                            .get(workflow.getCurrentStepIndex())
                            .getStepName();
        }


        return WorkflowStatusResponse
                .builder()
                .workflowId(workflow.getWorkflowId())
                .workflowName(workflow.getWorkflowName())
                .status(workflow.getStatus())
                .currentStep(workflow.getCurrentStepIndex())
                .stepName(currentStepName)
                .build();

    }

    @Override
    public FailureAnalysisResponse getFailureAnalysis(String workflowId){

        FailureAnalysisEntity entity = failureAnalysisRepository.findByWorkflowId(workflowId).orElseThrow();

        return FailureAnalysisResponse.builder()
                        .failureType(entity.getFailureType().name())
                        .rootCause(entity.getRootCause())
                        .impact(entity.getImpact())
                        .suggestedAction(entity.getSuggestedAction())
                        .build();

    }

    private void handleFailure(WorkflowEntity workflow, WorkflowStepEntity step, StepResultEvent result){

        step.setRetryCount(step.getRetryCount()+1);

        step.setErrorMessage(result.getErrorMessage());

        step.setFailureType(result.getFailureType());

        if(step.getRetryCount() <= step.getMaxRetries()){
            System.out.println("Retrying " + step.getStepName() + " Retry=" + step.getRetryCount());
            step.setStatus(StepStatus.RETRYING);
            workflowRepository.save(workflow);
            startCurrentStep(workflow);
            return;
        }

        sendToDLQ(result);
        failureAnalyzer.analyze(workflow, step, result);

        workflow.setStatus(WorkflowStatus.FAILED);

        step.setStatus(StepStatus.FAILED);

        workflowRepository.save(workflow);

    }

    private void sendToDLQ(StepResultEvent event){

        kafkaProducer.publishStep("workflow-dlq",

                WorkflowExecutionEvent
                        .builder()
                        .workflowId(event.getWorkflowId())
                        .stepName(event.getStepName())
                        .build()
        );

    }

    private void startCurrentStep(WorkflowEntity workflow){

        int currentIndex = workflow.getCurrentStepIndex();

        if(currentIndex >= workflow.getSteps().size()){
            workflow.setStatus(WorkflowStatus.SUCCESS);
            System.out.println("Workflow COMPLETED: " + workflow.getWorkflowName());

            return;
        }

        WorkflowStepEntity step = workflow
                        .getSteps()
                        .get(currentIndex);

        step.setStatus(StepStatus.RUNNING);

        WorkflowExecutionEvent event =
                WorkflowExecutionEvent
                        .builder()
                        .workflowId(workflow.getWorkflowId())
                        .stepId(step.getStepId())
                        .stepName(step.getStepName())
                        .build();

        kafkaProducer.publishStep(step.getTopic(), event);

    }

}
