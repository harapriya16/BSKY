package com.stl.bsky.entity.uac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name="status_master", schema="admin")
public class StatusMaster {

    @Id
    @Column(name = "status_id")
    private Integer statusId;

    @NotBlank(message = "Please provide status code")
    @Column(name = "status_code", unique = true)
    private String statusCode;

    @NotBlank(message = "Please provide status description")
    @Column(name = "status_description")
    private String statusDescription;

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
