package com.stl.bsky.model.mdm;

import java.util.List;

import lombok.Data;

@Data
public class GenCodeNameRepDto {
    private Long id;
    private int parentId;
    private String name;
    
    private List<GenCodeNameRepDto> childLists;

}
