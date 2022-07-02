package com.stl.bsky.model.uac;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EditRoleResourceRequest {
    @NotNull
    private int role_id;

    private List<ResourceInfoRequest> resource_info;
}
