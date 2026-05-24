package com.workflow.failureawareworkflow.controller;

import com.workflow.failureawareworkflow.dto.CreateWorkflowRequest;
import com.workflow.failureawareworkflow.dto.WorkflowStatusResponse;
import com.workflow.failureawareworkflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workflows")
public class WorkflowController {

    private final WorkflowService service;

    @PostMapping
    public UUID createWorkflow(@RequestBody CreateWorkflowRequest request){

        return service.createWorkflow(request);
    }

    @PostMapping("/{id}/execute")
    public void execute(@PathVariable UUID id){
        service.executeWorkflow(id);

    }

    @GetMapping("/{id}")
    public WorkflowStatusResponse status(@PathVariable UUID id){

        return service.getWorkflowStatus(id);

    }

}
