package com.workflow.failureawareworkflow.service.worker;

import com.workflow.failureawareworkflow.dto.StepResultEvent;
import com.workflow.failureawareworkflow.dto.WorkflowExecutionEvent;
import com.workflow.failureawareworkflow.enums.FailureType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class HotelWorker {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics="hotel-topic")
    public void execute(WorkflowExecutionEvent event){

        System.out.println("Booking Hotel");
//        Random random = new Random();
//
//        boolean success = random.nextBoolean();
        StepResultEvent result = StepResultEvent.builder()
                .workflowId(event.getWorkflowId())
                .stepId(event.getStepId())
                .stepName(event.getStepName())
                .success(false)
                .failureType(FailureType.TIMEOUT)
                .errorMessage("Hotel Api timeout")
                .build();
//        StepResultEvent result;
//
//        if(success){
//            result = StepResultEvent
//                            .builder()
//                            .workflowId(event.getWorkflowId())
//                            .stepId(event.getStepId())
//                            .stepName(event.getStepName())
//                            .success(true)
//                            .build();
//        }
//        else{
//            result = StepResultEvent
//                            .builder()
//                            .workflowId(event.getWorkflowId())
//                            .stepId(event.getStepId())
//                            .stepName(event.getStepName())
//                            .success(false)
//                            .failureType(FailureType.TIMEOUT)
//                            .errorMessage("Hotel API timeout")
//                            .build();
//
//        }

        kafkaTemplate.send("workflow-result", result);
    }
}
