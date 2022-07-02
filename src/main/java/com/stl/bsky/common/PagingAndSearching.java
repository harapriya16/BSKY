package com.stl.bsky.common;

import lombok.Data;

@Data
public class PagingAndSearching  {
    private Boolean isPaginationRequired;
    private Boolean isOrderRequired;
    private Boolean isAscending;
    private Integer pageNumber;
    private Integer pageSize;

    private String orderField;

    private Boolean isSearchRequired;
    private String key;
    //private String operation;
    private String value;

    private Object filter;

}
