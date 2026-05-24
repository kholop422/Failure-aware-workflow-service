package com.workflow.failureawareworkflow.exception;

import java.util.UUID;

public class WorkflowNotFoundException
        extends RuntimeException {

    public WorkflowNotFoundException(
            UUID workflowId
    ) {

        super(
                "Workflow not found: "
                        + workflowId
        );

    }

}
