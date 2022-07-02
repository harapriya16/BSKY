package com.stl.bsky.model.mdm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IconResponse {
    List<IconDetailsDTO> iconDetailsDTOList;
    private Integer totalPages;
    private Long totalElement;
}
