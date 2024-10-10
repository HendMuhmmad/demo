package com.example.demo.model.orm.workflow;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
@Table(name = "ECO_WF_TASKS")
public class WFTask {

    public WFTask(long instanceId, long assigneeId, String taskUrl, String assigneeRole,
	    Date assignDate) {
	this.instanceId = instanceId;
	this.assigneeId = assigneeId;
	this.assigneeRole = assigneeRole;
	this.assignDate = assignDate;
    }

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "ECO_TASKS_SEQ",
	    sequenceName = "ECO_TASKS_SEQ",
	    allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "ECO_TASKS_SEQ")
    private Long id;

    @Column(name = "INSTANCE_ID")
    private Long instanceId;

    @Column(name = "ASSIGNEE_ID")
    private Long assigneeId;

    @Column(name = "ASSIGN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignDate;

    @Column(name = "ASSIGNEE_ROLE")
    private String assigneeRole;

    @Column(name = "ACTION_ID")
    private Long actionId;

    @Column(name = "ACTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDate;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "REFUSE_REASONS")
    private String refuseReasons;

}