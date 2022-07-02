package com.stl.bsky.model.mdm;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class IconDetailsDTO {

    private Long id;

    private String iconType; 

    private String faText;
    
    private String image;
    
    private String imageName;

    @NotNull
    private int statusId;

}
