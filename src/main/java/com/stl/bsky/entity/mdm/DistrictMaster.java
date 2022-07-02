package com.stl.bsky.entity.mdm;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Entity
@Table(name = "mst_district", schema = "mdm")
public class DistrictMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
	private Integer id;

	@NotNull
	@Column(name = "district_code")
	private Integer districtCode;

	@NotBlank(message = "Please provide a district name")
	@Column(name = "district_name")
	private String districtName;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "state_code")
	private StateMaster stateMaster;
}
