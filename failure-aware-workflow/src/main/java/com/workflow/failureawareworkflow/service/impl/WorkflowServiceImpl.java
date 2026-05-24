package com.workflow.failureawareworkflow.service.impl;

import com.workflow.failureawareworkflow.dto.*;
import com.workflow.failureawareworkflow.entity.WorkflowEntity;
import com.workflow.failureawareworkflow.entity.WorkflowStepEntity;
import com.workflow.failureawareworkflow.enums.StepStatus;
import com.workflow.failureawareworkflow.enums.WorkflowStatus;
import com.workflow.failureawareworkflow.exception.WorkflowNotFoundException;
import com.workflow.failureawareworkflow.repository.WorkflowRepository;
import com.workflow.failureawareworkflow.service.WorkflowKafkaProducer;
import com.workflow.failureawareworkflow.service.WorkflowService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowKafkaProducer kafkaProducer;

    @Override
    public UUID createWorkflow(CreateWorkflowRequest request) {

        WorkflowEntity workflow = WorkflowEntity.builder()
                        .workflowName(request.getWorkflowName())
                        .status(WorkflowStatus.CREATED)
                        .currentStepIndex(0)
                        .createdAt(LocalDateTime.now())
                        .build();

        List<WorkflowStepEntity> steps =
                IntStream.range(
                                0,
                                request.getSteps().size()
                        )
                        .mapToObj(index -> {

                            WorkflowStepDTO dto =
                                    request.getSteps().get(index);

                            return WorkflowStepEntity
                                    .builder()
                                    .stepName(dto.getStepName())
                                    .topic(dto.getTopic())
                                    .executionOrder(index)
                                    .retryCount(0)
                                    .maxRetries(
                                            dto.getMaxRetries()
                                    )
                                    .status(
                                            StepStatus.PENDING
                                    )
                                    .workflow(workflow)
                                    .build();

                        })
                        .toList();

        workflow.setSteps(steps);

        WorkflowEntity saved = workflowRepository.save(workflow);

        return saved.getWorkflowId();

    }

    @Override
    public void executeWorkflow(UUID workflowId) {

        WorkflowEntity workflow = workflowRepository
                        .findById(workflowId)
                        .orElseThrow(() ->
                                        new WorkflowNotFoundException(workflowId)
                        );

        workflow.setStatus(WorkflowStatus.RUNNING);

        startCurrentStep(workflow);

        workflowRepository.save(workflow);

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
        }
        workflowRepository.save(workflow);

    }

    @Override
    public WorkflowStatusResponse getWorkflowStatus(UUID workflowId){

        WorkflowEntity workflow = workflowRepository
                        .findById(workflowId)
                        .orElseThrow(() ->
                                        new WorkflowNotFoundException(workflowId));

        return WorkflowStatusResponse
                .builder()
                .workflowId(workflow.getWorkflowId())
                .workflowName(workflow.getWorkflowName())
                .status(workflow.getStatus())
                .currentStep(workflow.getCurrentStepIndex())
                .build();

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
