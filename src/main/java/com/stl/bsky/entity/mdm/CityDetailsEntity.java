package com.stl.bsky.entity.mdm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "mst_city", schema = "mdm")
public class CityDetailsEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @NotBlank
    @Column(name = "city_name")
	private String cityName;

    @ManyToOne
    @JsonIgnoreProperties("districtMaster")
    @JoinColumn(name = "state_code")
    private StateMaster stateMaster;

}
