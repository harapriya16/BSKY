package com.stl.bsky.entity.uac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "role_master", schema = "admin")
public class RoleMaster {

    @Id
    @Column(name = "id")
    private Integer roleId;

    @NotBlank(message = "Role name is mandatory")
    @Column(name = "role_name", unique = true)
    private String roleName;

    @NotBlank(message = "Role code is mandatory")
    @Column(name = "role_code", unique = true)
    private String roleCode;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role", fetch = FetchType.LAZY)
    private List<UserMaster> users;

    @ManyToOne
    @JoinColumn(name = "status_id")
    @JsonIgnoreProperties("statusDescription")
    private StatusMaster status;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = "admin",
            name = "role_resource",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private List<ResourceMaster> resources;

    @JsonIgnore
    @Column(name = "created_by")
    private String createdBy;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_dt", updatable = false)
    private Date createdDt;

    @JsonIgnore
    @Column(name = "updated_by")
    private String updatedBy;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_dt")
    private Date updatedDt;
}
