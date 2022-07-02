package com.stl.bsky.model.uac;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.stl.bsky.common.FileDTO;
import com.stl.bsky.entity.mdm.IconDetails;
import com.stl.bsky.entity.uac.StatusMaster;

import lombok.Data;

@Data
public class AddResourceRequest {

    private Long resourceId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

//    private String resource_type;

    private Long parentResourceId;

//    private String resource_url;

    @NotNull
    private Boolean hasSubMenu;

//    private String router_link;

//    private String target;

//    private String resource_icon;

    private String icon;

    @NotNull
    @Min(value = 1, message = "Minimum level must be 1")
    private int level;

    private String path;

    @NotNull
    private int statusId;

    @NotNull
    @Min(value = 1, message = "Minimum orderIn must be 1")
    private int orderIn;
    
    
    private StatusMaster status;
    
    private String iconType;
    
    private IconDetails iconDetails;

}
