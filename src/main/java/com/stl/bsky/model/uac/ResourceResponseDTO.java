package com.stl.bsky.model.uac;

import lombok.Data;

import java.util.Date;
import java.util.List;

import com.stl.bsky.entity.mdm.IconDetails;
import com.stl.bsky.entity.uac.StatusMaster;

@Data
public class ResourceResponseDTO {

    private Long resourceId;

    private String name;

    private String description;

    private Long parentResourceId;

    private boolean hasSubMenu;

    private String path;

    private String icon;

    private int level;

    private StatusMaster status;

    private String createdBy;

    private Date createdDt;

    private String updatedBy;

    private String updatedDt;

    private boolean mapped;

    private int orderIn;

    private List<ResourceResponseDTO> responseDTOs;
    
    private String iconType;
    
    private IconDetails iconDetails;

}
