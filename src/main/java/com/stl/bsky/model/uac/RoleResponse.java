package com.stl.bsky.model.uac;

import lombok.Data;

import java.util.List;

import com.stl.bsky.entity.uac.RoleMaster;

@Data
public class RoleResponse {
    private List<RoleMaster> roleResponse;
    private Integer totalPages;
    private Long totalElement;
}
