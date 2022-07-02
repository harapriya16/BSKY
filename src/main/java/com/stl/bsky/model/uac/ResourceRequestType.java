package com.stl.bsky.model.uac;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResourceRequestType {
    private String flatOrNested;
    private int roleId;
    private boolean mappedAndAll;
}
