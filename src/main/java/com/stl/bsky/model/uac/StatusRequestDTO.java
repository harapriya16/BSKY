package com.stl.bsky.model.uac;

import lombok.Data;

@Data
public class StatusRequestDTO {
    private int statusId;
    private String statusCode;
    private String statusDesc;
}
