package com.workflow.failureawareworkflow.service;

import com.workflow.failureawareworkflow.dto.WorkflowExecutionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishStep(
            String topic,
            WorkflowExecutionEvent event){

        kafkaTemplate.send(topic, event);

    }

}
