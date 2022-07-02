package com.stl.bsky.entity.mdm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "mst_post_office", schema = "mdm")
public class PostOfficeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",nullable = false)
	private Long id;

	@Column(name = "pin_code")
	private Long pinCode;

	@Column(name = "post_office_name")
	private String postOfficeName;
	@ManyToOne
	@JsonIgnoreProperties("districtMaster")
	@JoinColumn(name = "state_code")
	private StateMaster stateMaster;
	@ManyToOne
	@JoinColumn(name = "district_id")
	private DistrictMaster districtMaster;
}