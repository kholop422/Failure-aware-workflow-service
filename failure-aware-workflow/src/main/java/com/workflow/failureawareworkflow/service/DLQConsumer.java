package com.workflow.failureawareworkflow.service;

import com.workflow.failureawareworkflow.dto.WorkflowExecutionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DLQConsumer {

    @KafkaListener(topics="workflow-dlq")
    public void consume(WorkflowExecutionEvent event){

        System.out.println("DLQ EVENT : " + event.getStepName());

    }

}