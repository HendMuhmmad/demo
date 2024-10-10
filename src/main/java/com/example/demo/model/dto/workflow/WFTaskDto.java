package com.example.demo.model.dto.workflow;

import java.util.Date;

import lombok.Data;

@Data
public class WFTaskDto<T> {

	private Long id;
	private Long instanceId;
	private Long assigneeId;
	private Date assignDate;
	private String assigneeRole;
	private String notes;
	private String refuseReasons;
	T request;

	public WFTaskDto(Long id, Long instanceId, Long assigneeId, Date assignDate, String assigneeRole, String notes,
			String refuseReasons, T request) {
		this.id = id;
		this.instanceId = instanceId;
		this.assigneeId = assigneeId;
		this.assignDate = assignDate;
		this.assigneeRole = assigneeRole;
		this.notes = notes;
		this.refuseReasons = refuseReasons;
		this.request = request;
	}

}
