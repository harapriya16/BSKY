package com.stl.bsky.service.uac;

import com.stl.bsky.common.PagingAndSearching;
import com.stl.bsky.entity.uac.RoleMaster;
import com.stl.bsky.entity.uac.StatusMaster;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.model.uac.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface UacService {
    ResponseEntity<Object> createOrUpdateUser(UserMaster request, Authentication authentication);

//    ResponseEntity<Object> editUser(UserMaster userMaster, Authentication authentication);

//    ResponseEntity<Object> editRole(RoleMaster roleMaster, Authentication authentication);

    ResponseEntity<Object> addOrUpdateRole(RoleMaster roleMaster, Authentication authentication);

    ResponseEntity<Object> addResource(AddResourceRequest request, Authentication authentication);

    ResponseEntity<Object> getAllResource(String flatOrNested);

    ResponseEntity<Object> getAllResourcesByRoleId(int roleId);

    ResponseEntity<Object> getMappedAndAllResourceDetails(int roleId);

    ResponseEntity<Object> addRoleResource(EditRoleResourceRequest request, Authentication authentication);

    ResponseEntity<Object> addStatus(StatusMaster statusMaster, Authentication authentication);

    ResponseEntity<Object> getAllStatus();

    ResponseEntity<Object> getAllUsers(PagingAndSearching pagination);

    ResponseEntity<Object> getAllRoles(PagingAndSearching pagination);

	ResponseEntity<Object> getAllResourcesByAuthenticatingRole(HttpServletRequest request);
}
