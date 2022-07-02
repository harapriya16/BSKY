package com.stl.bsky.mapper;

import com.stl.bsky.entity.uac.ResourceMaster;
import com.stl.bsky.entity.uac.RoleMaster;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.model.uac.AddResourceRequest;
import com.stl.bsky.model.uac.ResourceResponseDTO;
import com.stl.bsky.model.uac.RoleResponseDTO;
import com.stl.bsky.model.uac.UserInfo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel="spring")
public interface UacMapper {
    UacMapper INSTANCE = Mappers.getMapper(UacMapper.class);

    @Mappings({
            @Mapping(target="createdOn", source="createdDt"),
            @Mapping(target="updatedOn", source="updatedDt"),
            @Mapping(target="mobileNo", source="contactNo"),
            @Mapping(target="roleId", source = "role.roleId"),
            @Mapping(target="statusId", source = "status.statusId"),
    })
    UserInfo convertUserMasterToUserResponseDTO(UserMaster userMaster);

    List<UserInfo> convertUserMasterToUserResponseDTO(Stream<UserMaster> userMasterList);

    @Mapping(target = "statusId", source = "status.statusId")
    RoleResponseDTO convertRoleMasterToRoleResponseDTO(RoleMaster roleMaster);

    List<RoleResponseDTO> convertRoleMasterToRoleResponseDTO(List<RoleMaster> roleMasters);

    ResourceMaster convertResourceDTOToResourceMaster(AddResourceRequest addResourceRequest);

    @Mappings({
            @Mapping(target="createdDt", source="createdDt", dateFormat="yyyy-MM-dd HH:mm"),
            @Mapping(target="updatedDt", source="updatedDt", dateFormat="yyyy-MM-dd HH:mm")
    })
    ResourceResponseDTO convertResourceMasterToResourceResponseDTO(ResourceMaster resourceMaster);

}
