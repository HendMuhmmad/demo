package com.example.demo.model.orm.workflow;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ECO_TASKS")
public class WFTask {

    public WFTask(long instanceId, long originalId, long assigneeId, String taskUrl, Long assigneeRole, Date assignDate) {
	this.instanceId = instanceId;
	this.originalId = originalId;
	this.assigneeId = assigneeId;
	this.assigneeRole = assigneeRole;
	this.assignDate = assignDate;
    }

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "INSTANCE_ID")
    private Long instanceId;

    @Column(name = "ASSIGNEE_ID")
    private Long assigneeId;

    @Column(name = "ORIGINAL_ID")
    private Long originalId;

    @Column(name = "ASSIGN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignDate;

    @Column(name = "ASSIGNEE_ROLE")
    private Long assigneeRole;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "ACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDate;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "REFUSE_REASONS")
    private String refuseReasons;

}