package com.stl.bsky.model.uac;

import lombok.Data;

import java.util.Date;

@Data
public class RoleResponseDTO {

    private int roleId;

    private String roleName;

    private String roleCode;

    private int statusId;

    private String createdBy;

    private Date createdDt;

    private String updatedBy;

    private Date updatedDt;
}
