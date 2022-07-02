package com.stl.bsky.model.uac;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.stl.bsky.entity.uac.UserMaster;

@Getter
@Setter
public class UserListResponseDTO {

    private List<UserMaster> userListResponse;

    private Integer totalPages;
    private Long totalElement;
}
