package com.stl.bsky.entity.mdm;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "mst_state", schema = "mdm")
public class StateMaster {
    @Id
    @Column(name = "state_code", nullable = false)
    @Size(min = 2, max = 2, message = "State code must contain 2 digits")
    private String stateCode;

    @NotBlank(message = "Please provide a state name")
    @Column(name = "state_name")
    private String stateName;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "stateMaster")
    @ToString.Exclude
    private List<@Valid DistrictMaster> districtMaster;
}
