package com.example.demo.model.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class WFTaskDetailsDto<T> {
    private Long wfProcessId;
    private Long wfInstanceId;
    private Long assigneeId;
    private String assigneeRole;
    private Boolean isRunning;
    private T request;
}
