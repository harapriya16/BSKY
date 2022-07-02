package com.stl.bsky.model.uac;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddRoleRequest {

    @NotBlank
    private String roleName;

    @NotBlank
    private String roleCode;

    @NotBlank
    private int statusId;

}
