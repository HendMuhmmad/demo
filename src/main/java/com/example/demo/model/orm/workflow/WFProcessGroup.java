package com.example.demo.model.orm.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ECO_WF_PROCESSES_GROUPS")
public class WFProcessGroup {

	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "ECO_PROCESSES_GROUPS_SEQ", sequenceName = "ECO_PROCESSES_GROUPS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ECO_PROCESSES_GROUPS_SEQ")
	private Long id;

	@Column(name = "NAME")
	private String processGroupName;
}
