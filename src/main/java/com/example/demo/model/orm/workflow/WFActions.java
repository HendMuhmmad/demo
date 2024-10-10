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

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "ECO_WF_ACTIONS")
public class WFActions {

    @Id
	@SequenceGenerator(name = "ECO_WF_ACTIONS_SEQ", sequenceName = "ECO_WF_ACTIONS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ECO_WF_ACTIONS_SEQ")
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACTION_NAME")
    private String actionName;
}