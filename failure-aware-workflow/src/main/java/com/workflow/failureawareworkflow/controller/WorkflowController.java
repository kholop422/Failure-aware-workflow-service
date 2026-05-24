package com.workflow.failureawareworkflow.controller;

import com.workflow.failureawareworkflow.dto.*;
import com.workflow.failureawareworkflow.service.FailureAnalyzerService;
import com.workflow.failureawareworkflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workflows")
public class WorkflowController {

    private final WorkflowService service;
    private final FailureAnalyzerService failureAnalyzerService;

    @PostMapping
    public ResponseEntity<WorkflowCreateResponse> createWorkflow(@RequestBody CreateWorkflowRequest request){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createWorkflow(request));

    }
    @PostMapping("/{id}/execute")
    public ResponseEntity<WorkflowExecutionResponse> execute(@PathVariable String id){

        return ResponseEntity.ok(service.executeWorkflow(id));

    }
    @GetMapping("/{id}")
    public WorkflowStatusResponse status(@PathVariable String id){

        return service.getWorkflowStatus(id);
    }

    @GetMapping("/{id}/failure-analysis")
    public FailureAnalysisResponse analysis(@PathVariable String id){
        return failureAnalyzerService.getFailureAnalysis(id);
    }

}
