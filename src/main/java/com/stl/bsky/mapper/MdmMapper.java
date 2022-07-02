package com.stl.bsky.mapper;

import com.stl.bsky.entity.mdm.GenCodeEntity;
import com.stl.bsky.entity.mdm.IconDetails;
import com.stl.bsky.model.mdm.GenCodeNameRepDto;
import com.stl.bsky.model.mdm.IconDetailsDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel="spring")
public interface MdmMapper {
    MdmMapper INSTANCE = Mappers.getMapper(MdmMapper.class );

    @Mappings({
            @Mapping(source = "status.statusId", target = "statusId"),
    })
    IconDetailsDTO toIconDTOFromIconEntity(IconDetails iconDetailsStream);

    List<IconDetailsDTO> toIconDTOFromIconEntity(Stream<IconDetails> iconDetailsStream);

	List<GenCodeNameRepDto> convertGenEntityToDTO(List<GenCodeEntity> findByParentIdAndIsActive);

	@Mappings({
        @Mapping(source = "statusId", target = "status.statusId"),
    })
	IconDetails convertIconDetailsDTOToEntity(IconDetailsDTO iconDetailsDTO);

	List<IconDetailsDTO> toIconDetailsDTOList(Stream<IconDetails> stream);
}
