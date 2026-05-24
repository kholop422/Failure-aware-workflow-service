package com.workflow.failureawareworkflow.service.impl;

import com.workflow.failureawareworkflow.dto.OllamaRequest;
import com.workflow.failureawareworkflow.dto.OllamaResponse;
import com.workflow.failureawareworkflow.entity.FailureAnalysisEntity;
import com.workflow.failureawareworkflow.entity.WorkflowEntity;
import com.workflow.failureawareworkflow.entity.WorkflowStepEntity;
import com.workflow.failureawareworkflow.service.AIAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIAnalysisServiceImpl
        implements AIAnalysisService {

    private final RestTemplate
            restTemplate;

    @Override
    public String generateDiagnostic(
            FailureAnalysisEntity entity,
            WorkflowEntity workflow,
            WorkflowStepEntity step) {

        log.info("Generating Ai diagnostics");
        String prompt = buildPrompt(
                entity,
                workflow,
                step
        );

        OllamaRequest request = OllamaRequest.builder()
                        .model("phi3:mini")
                        .prompt(prompt)
                        .stream(false)
                        .build();

        OllamaResponse response = restTemplate.postForObject("http://localhost:11434/api/generate",
                        request,
                        OllamaResponse.class
                        );

        return sanitizeAIResponse(response.getResponse());

    }

    private String sanitizeAIResponse(String response){

        if(response == null){
            return "{}";

        }

        response = response.trim();

        response = response.replace("```json", "");

        response = response.replace("```", "");

        int start = response.indexOf("{");

        int end = response.lastIndexOf("}");

        if(start != -1 && end != -1){
            response = response.substring(start, end + 1);
        }

        return response.trim();

    }


    private String buildPrompt(FailureAnalysisEntity entity,
                               WorkflowEntity workflow,
                               WorkflowStepEntity step){

        return """
                
                You are an SRE assistant for a distributed workflow orchestration platform.
                
                Use ONLY information explicitly provided.
                
                Never invent:
                - stakeholders
                - customers
                - bypass mechanisms
                - infrastructure details
                - alternative systems
                - monitoring systems
                
                Workflow:
                
                Name: %s
                
                Failed Step: %s
                
                Failure Type: %s
                
                Retries Attempted: %d
                
                Workflow Status: %s
                
                Known Root Cause: %s
                
                Retry Exhausted: %s
                
                Output STRICT JSON ONLY:
                
                {
                "rootCause":"",
                "operationalImpact":"",
                "recoveryRecommendation":""
                }
                
                Rules:
                
                - Use ONLY provided information
                - Never mention customers
                - Never mention SLAs
                - Never mention escalation protocols
                - Never invent timelines
                - Never invent monitoring systems
                - Never invent operational procedures
                
                Allowed recovery actions:
                
                - retry workflow
                - inspect downstream dependency
                - verify dependency availability
                - inspect timeout configuration
                
                Operational impact MUST only describe:
                
                - workflow halted
                - dependent steps not executed
                - delayed workflow completion
                - retry exhaustion
                
                Keep response concise.
                
                """

                .formatted(

                        workflow.getWorkflowName(),

                        step.getStepName(),

                        entity.getFailureType(),

                        step.getRetryCount(),

                        workflow.getStatus(),

                        entity.getRootCause(),

                        step.getRetryCount() >=
                                step.getMaxRetries()

                );

    }

}
