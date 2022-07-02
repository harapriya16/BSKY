package com.stl.bsky.model.uac;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EditRoleRequest {

    @NotNull
    private int roleId;
    private String roleName;
    private String roleCode;
    private int statusId;
}
