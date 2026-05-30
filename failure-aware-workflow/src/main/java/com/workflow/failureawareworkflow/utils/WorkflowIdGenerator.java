package com.workflow.failureawareworkflow.utils;

import java.util.UUID;

public class WorkflowIdGenerator {

    private static final String PREFIX =
            "WORKFLOW_";

    public static String generate(){

        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 6)
                        .toUpperCase();

        return PREFIX + random;

    }

}