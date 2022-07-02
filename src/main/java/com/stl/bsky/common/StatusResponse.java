package com.stl.bsky.common;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {
    private int status;
    private Object statusDesc;
    private Object data;

    public StatusResponse(int status, Object statusDesc) {
        this.status = status;
        this.statusDesc = statusDesc;
    }
}
