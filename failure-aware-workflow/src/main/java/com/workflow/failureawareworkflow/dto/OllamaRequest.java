package com.workflow.failureawareworkflow.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaRequest {

    private String model;

    private String prompt;

    private Boolean stream;

}