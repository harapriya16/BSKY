package com.stl.bsky.entity.uac;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stl.bsky.entity.mdm.IconDetails;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "resource_master", schema = "admin",
        uniqueConstraints = @UniqueConstraint(columnNames = {"parent_resource_id", "order_in"}))
public class ResourceMaster {

    @Id
    @Column(name = "id")
    private Long resourceId;

    @NotBlank(message = "Resource name cannot be empty")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Resource description cannot be empty")
    @Column(name = "description")
    private String description;

    @Column(name = "path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "parent_resource_id")
    private ResourceMaster parentResource;

    @Column(name = "level")
    private Integer level;

    @Column(name = "icon")
    private String icon;

    @ManyToOne
    @JoinColumn(name = "status_id")
    @JsonIgnoreProperties({"statusCode", "statusDescription"})
    private StatusMaster status;

    @NotNull(message = "Please mention if resource has sub menu")
    @Column(name = "has_sub_menu")
    private Boolean hasSubMenu;

    @JsonIgnore
    @ManyToMany(mappedBy = "resources", fetch = FetchType.LAZY)
    private List<RoleMaster> roles;

    @NotNull(message = "Order cannot be null")
    @Column(name = "order_in", columnDefinition = "integer default 0")
    private Integer orderIn;

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
    
   
    
    @Column(name = "icon_type")
    private String iconType;
    
    @ManyToOne
    @JoinColumn(name = "icon_id")
    private IconDetails iconDetails;
}
