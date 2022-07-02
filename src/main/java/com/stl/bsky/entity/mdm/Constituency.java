package com.stl.bsky.entity.mdm;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "constituency", schema = "mdm")
public class Constituency {
	
    @Id
    private Integer constituencyCode;

    private String constituencyName;

    @ManyToOne
    @JoinColumn(name = "id")
    private DistrictMaster districtMaster;
}
