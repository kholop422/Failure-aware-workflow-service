package com.workflow.failureawareworkflow.service.worker;

import com.workflow.failureawareworkflow.dto.StepResultEvent;
import com.workflow.failureawareworkflow.dto.WorkflowExecutionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelWorker {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics="hotel-topic")
    public void execute(WorkflowExecutionEvent event){

        System.out.println("Booking Hotel");

        StepResultEvent result = StepResultEvent.builder()
                .workflowId(event.getWorkflowId())
                .stepId(event.getStepId())
                .stepName(event.getStepName())
                .success(true)
                .build();

        kafkaTemplate.send("workflow-result", result);
    }
}
