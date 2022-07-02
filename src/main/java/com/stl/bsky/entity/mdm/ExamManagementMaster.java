package com.stl.bsky.entity.mdm;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.stl.bsky.entity.uac.StatusMaster;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "exam_management_master", schema = "mdm")
public class ExamManagementMaster{
	@Id
    @Column(name = "id")
    private Long id;
	
	@Column(name = "exam_mgmt_name")
    private String examManagementName;
	
	@ManyToOne
	@JoinColumn(name = "status_id")
    private StatusMaster status;
	
	@Column(name = "flag")
	private Boolean flag;
	

}
