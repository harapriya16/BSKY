package com.stl.bsky.controller.uac;

import com.stl.bsky.common.CommonUtil;
import com.stl.bsky.common.Constants;
import com.stl.bsky.common.PagingAndSearching;
import com.stl.bsky.common.StatusResponse;
import com.stl.bsky.entity.uac.RoleMaster;
import com.stl.bsky.entity.uac.StatusMaster;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.event.OnUserLogoutSuccessEvent;
import com.stl.bsky.model.uac.*;
import com.stl.bsky.service.uac.UacService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "UacController", description = "UAC rest controller")
@RestController
@RequestMapping(Constants.BASE_PATH + "/uac")
@Slf4j
public class UacController {

    @Autowired
    UacService uacService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Operation(summary = "End-point to fetch list of active users")
//    @ApiResponse(content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    @PostMapping("/getAllUsers")
    public ResponseEntity<Object> getAllUsers(@RequestBody PagingAndSearching pagination) {
        if(pagination.getIsPaginationRequired() && (null == pagination.getPageNumber() || 0 == pagination.getPageNumber() ||
                null == pagination.getPageSize() || 0 == pagination.getPageSize()))
            return new ResponseEntity<>(new StatusResponse(0, "Page Number/Page Size is required"), HttpStatus.BAD_REQUEST);
        else if(pagination.getIsOrderRequired() && !CommonUtil.hasFieldExist(pagination.getOrderField(), UserInfo.class))
            return new ResponseEntity<>(new StatusResponse(0, "Order field name not found "), HttpStatus.BAD_REQUEST);
        else if(pagination.getIsSearchRequired() && !CommonUtil.hasFieldExist(pagination.getKey(), UserInfo.class))
            return new ResponseEntity<>(new StatusResponse(0, " Search field name not found "), HttpStatus.BAD_REQUEST);
        else {
            pagination.setPageNumber(pagination.getPageNumber()-1);
            return uacService.getAllUsers(pagination);
        }
    }

    @PreAuthorize("hasRole('ROLE_ORGADMIN')")
    @Operation(summary = "End-point to create user")
    @PostMapping("/createOrUpdateUser")
    public ResponseEntity<Object> createOrUpdateUser(@Valid @RequestBody UserMaster userMaster, BindingResult bindingResult,
                                             Authentication authentication) {
        if (bindingResult.hasErrors()) {
            log.error("error " + bindingResult.getAllErrors());
            log.error("error::::"+bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return uacService.createOrUpdateUser(userMaster, authentication);
    }

/*    @PreAuthorize("hasRole('ROLE_ORGADMIN')")
    @Operation(summary = "End-point to edit user")
    @PutMapping(value = "/editUser")
    public ResponseEntity<Object> editUser(@Valid @RequestBody UserMaster userMaster,
                                           BindingResult bResult, Authentication authentication) {
        System.err.println("userMaster " + userMaster.toString());
        if (bResult.hasErrors()) {
            StatusResponse response = new StatusResponse();
            response.setStatus(0);
            response.setStatusDesc("Given information is not correct. Errors:" + bResult.getAllErrors());
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return uacService.editUser(userMaster, authentication);
    }*/

    @PreAuthorize("hasRole('ROLE_ORGADMIN')")
    @Operation(summary = "End-point to add role")
    @PostMapping(value = "/addOrUpdateRole")
    public ResponseEntity<Object> addOrUpdateRole(@Valid @RequestBody RoleMaster roleMaster, Authentication authentication) {

        return uacService.addOrUpdateRole(roleMaster, authentication);
    }

    @Operation(summary = "End-point to fetch all roles")
    @PostMapping(value = "/getAllRoles")
    public ResponseEntity<Object> getRoles(@RequestBody PagingAndSearching pagination, Authentication authentication) {
        if(pagination.getIsPaginationRequired() && (null == pagination.getPageNumber() || 0 == pagination.getPageNumber() ||
                null == pagination.getPageSize() || 0 == pagination.getPageSize()))
            return new ResponseEntity<>(new StatusResponse(0, "Page Number/Page Size is required"), HttpStatus.BAD_REQUEST);
        else if(pagination.getIsOrderRequired() && !CommonUtil.hasFieldExist(pagination.getOrderField(), RoleResponseDTO.class))
            return new ResponseEntity<>(new StatusResponse(0, "Order field name not found "), HttpStatus.BAD_REQUEST);
        else if(pagination.getIsSearchRequired() && !CommonUtil.hasFieldExist(pagination.getKey(), RoleResponseDTO.class))
            return new ResponseEntity<>(new StatusResponse(0, " Search field name not found "), HttpStatus.BAD_REQUEST);
        else {
            pagination.setPageNumber(pagination.getPageNumber() - 1);
            return uacService.getAllRoles(pagination);
        }
    }

/*    @PreAuthorize("hasRole('ROLE_ORGADMIN')")
    @Operation(summary = "End-point to edit role")
    @PutMapping(value = "/editRole")
    public ResponseEntity<Object> editRole(@Valid @RequestBody RoleMaster roleMaster,
                                           BindingResult bResult, Authentication authentication) {
        if (bResult.hasErrors()) {
            StatusResponse response = new StatusResponse();
            response.setStatus(0);
            response.setStatusDesc("Given information is not correct. Errors:" + bResult.getAllErrors());
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return uacService.editRole(roleMaster, authentication);
    }*/

    @PreAuthorize("hasRole('ROLE_ORGADMIN')")
    @Operation(summary = "End-point to add resource")
    @PostMapping(value = "/addResource")
    public ResponseEntity<Object> addResource(@RequestBody @Valid AddResourceRequest request,
                                              BindingResult bresult, Authentication authentication) {

        if (bresult.hasErrors()) {
            return new ResponseEntity<>(new StatusResponse(0,
                    bresult.getAllErrors().get(0).getDefaultMessage()), HttpStatus.BAD_REQUEST);
        } else {
            return uacService.addResource(request, authentication);
        }
    }

    @Operation(summary = "End-point to fetch all resources")
    @PostMapping(value = "/getAllResource")
    public ResponseEntity<Object> getAllResource(@Valid @RequestBody ResourceRequestType resourceRequestType, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity<>(new StatusResponse(0, "Please send valid data"), HttpStatus.BAD_REQUEST);
        }

        if(0 != resourceRequestType.getRoleId()) {
            int roleId = 1;
            if(resourceRequestType.isMappedAndAll()) {
                return uacService.getMappedAndAllResourceDetails(resourceRequestType.getRoleId());
//                return uacService.getMappedAndAllResourceDetails(roleId);
            } else {
                return uacService.getAllResourcesByRoleId(resourceRequestType.getRoleId());
//                return uacService.getAllResourcesByRoleId(roleId);
            }
        } else {
            return uacService.getAllResource(resourceRequestType.getFlatOrNested());
        }
    }

    @Operation(summary = "End-point to fetch all resources by role")
    @GetMapping(value = "/getAllResourceByAuthenticatingRole")
    public ResponseEntity<Object> getAllResourceByAuthenticatingRole(HttpServletRequest request ) {

        return uacService.getAllResourcesByAuthenticatingRole(request);
    }

    @PreAuthorize("hasRole('ROLE_ORGADMIN')")
    @Operation(summary = "End-point to map role resource")
    @PostMapping(value = "/addRoleResource")
    public ResponseEntity<Object> mapRoleResource(
            @Valid @RequestBody EditRoleResourceRequest request, BindingResult bResult, Authentication authentication) {
        if (bResult.hasErrors()) {
            StatusResponse response = new StatusResponse();
            response.setStatus(0);
            response.setStatusDesc("Given information is not correct. Errors:" + bResult.getAllErrors());
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        return uacService.addRoleResource(request, authentication);
    }

    @PreAuthorize("hasRole('ROLE_ORGADMIN')")
    @Operation(summary = "End-point to add status")
    @PostMapping(value = "/addStatus")
    public ResponseEntity<Object> addStatus(@RequestBody StatusMaster statusMaster,
                                            BindingResult bResult, Authentication authentication) {

        if (bResult.hasErrors()) {
            return new ResponseEntity<>(new StatusResponse(0, "Please send valid data"), HttpStatus.BAD_REQUEST);
        } else {
            return uacService.addStatus(statusMaster, authentication);
        }
    }

    @Operation(summary = "End-point to fetch all status")
    @GetMapping(value = "/getAllStatus")
    public ResponseEntity<Object> getAllStatus() {
        return uacService.getAllStatus();
    }

    @PutMapping("/logout")
    public ResponseEntity<Object> logoutUser(@Valid @RequestBody LogoutRequest logoutRequest) {

        OnUserLogoutSuccessEvent logoutSuccessEvent = new OnUserLogoutSuccessEvent(logoutRequest.getUserName(), logoutRequest.getToken(), logoutRequest);
        applicationEventPublisher.publishEvent(logoutSuccessEvent);
        return ResponseEntity.ok(new StatusResponse(1, "User has successfully logged out from the system!"));
    }

}
