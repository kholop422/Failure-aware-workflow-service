package com.workflow.failureawareworkflow.service;


import com.workflow.failureawareworkflow.dto.StepResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowResultConsumer {

    private final WorkflowService workflowService;

    @KafkaListener(topics="workflow-result")
    public void consume(StepResultEvent result){

        workflowService.handleResult(result);

    }

}