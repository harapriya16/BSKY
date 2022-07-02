package com.stl.bsky.controller.mdm;

import com.stl.bsky.common.CommonUtil;
import com.stl.bsky.common.Constants;
import com.stl.bsky.common.PagingAndSearching;
import com.stl.bsky.common.StatusResponse;
import com.stl.bsky.exception.EmptyInputException;
import com.stl.bsky.model.mdm.IconDetailsDTO;
import com.stl.bsky.service.mdm.MdmService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "mdmRestController", description = "This module is responsible to expose master APIs")
@RestController
@RequestMapping(Constants.BASE_PATH + "/mdm")
@Slf4j
public class MdmController {
    @Autowired
    MdmService mdmService;
    
    

    @Operation(summary = "End-point to add icon")
    @PostMapping("/createIcon")
    public ResponseEntity<Object> createIcon(@Valid @RequestBody IconDetailsDTO iconDetailsDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new EmptyInputException("Please send proper details");
            // return new ResponseEntity<>(new StatusResponse(-1, "Please send proper details"), HttpStatus.NOT_ACCEPTABLE);
        }
        return mdmService.createIcon(iconDetailsDTO);
    }

    @Operation(summary = "End-point to view Icons")
    @PostMapping("/getAllIcons")
    public ResponseEntity<Object> getAllIcons(@RequestBody PagingAndSearching pagination, HttpServletRequest request, HttpServletResponse response) {

        if (pagination.getIsPaginationRequired() && (null == pagination.getPageSize() || pagination.getPageSize() == 0 || null == pagination.getPageNumber() || pagination.getPageNumber() == 0)) {
            return new ResponseEntity<>(new StatusResponse(0, "Page Number/Page Size is Required"), HttpStatus.BAD_REQUEST);
        } else if (pagination.getIsOrderRequired() && !CommonUtil.hasFieldExist(pagination.getOrderField(), IconDetailsDTO.class)) {
            return new ResponseEntity<>(new StatusResponse(0, "Order field name not found"), HttpStatus.BAD_REQUEST);
        } else if (pagination.getIsSearchRequired() && !CommonUtil.hasFieldExist(pagination.getKey(), IconDetailsDTO.class)) {
            return new ResponseEntity<>(new StatusResponse(0, " Search field name not found "), HttpStatus.BAD_REQUEST);
        } else {
            pagination.setPageNumber(pagination.getPageNumber() - 1);
            return new ResponseEntity<>(mdmService.getAllIconsWithSpecification(pagination), HttpStatus.OK);
        }
    }
   
    @Operation(summary = "End-point to get Access control Entity type details")
    @GetMapping("/getAccessControlEntityTypeDetails")
    public ResponseEntity<?> getAccessControlEntityTypeDetails() {
        return mdmService.getAccessControlEntityTypeDetails();
    }

    @Operation(summary = "End-point to get Access control Entity type details by giving Id")
    @GetMapping("/getAccessControlEntityTypeDetailsById/{id}")
    public ResponseEntity<?> getAccessControlEntityTypeDetailsById(@PathVariable Long id) {
        return mdmService.getAccessControlEntityTypeDetailsById(id);
    }

    @Operation(summary = "End-point to get State Details")
    @GetMapping("/getStateDetails")
    public ResponseEntity<?> getStateDetails() {
        return mdmService.getStateDetails();
    }

    @Operation(summary = "End-point to get State Details by giving state code")
    @GetMapping("/getStateDetailsByStateCode/{stateCode}")
    public ResponseEntity<?> getStateDetailsByStateCode(@PathVariable String stateCode) {
        return mdmService.getStateDetailsByStateCode(stateCode);
    }

    @Operation(summary = "End-point to get City Details")
    @GetMapping("/getCityDetails")
    public ResponseEntity<?> getCityDetails() {
        return mdmService.getCityDetails();
    }

    @Operation(summary = "End-point to get City Details by giving Id")
    @GetMapping("/getCityDetailsById/{id}")
    public ResponseEntity<?> getCityDetailsById(@PathVariable Long id) {
        return mdmService.getCityDetailsById(id);
    }

    @Operation(summary = "End-point to get City Details by giving state code")
    @GetMapping("/getCityDetailsByStateCode/{stateCode}")
    public ResponseEntity<?> getCityDetailsByStateCode(@PathVariable String stateCode) {
        return mdmService.getCityDetailsByStateCode(stateCode);
    }

    @Operation(summary = "End-point to get Post Office Details")
    @GetMapping("/getPostOfficeDetails")
    public ResponseEntity<?> getPostOfficeDetails() {
        return mdmService.getPostOfficeDetails();
    }

    @Operation(summary = "End-point to get Post Office Details by Pin code")
    @GetMapping("/getPostOfficeDetailsByPinCode/{pinCode}")
    public ResponseEntity<?> getPostOfficeDetailsByPinCode(@PathVariable Long pinCode) {
        return mdmService.getPostOfficeDetailsByPinCode(pinCode);
    }

    @Operation(summary = "End-point to get District Details")
    @GetMapping("/getDistrictDetails")
    public ResponseEntity<?> getDistrictDetails() {
        return mdmService.getDistrictDetails();
    }

    @Operation(summary = "End-point to get District Details by giving Id")
    @GetMapping("/getDistrictDetailsById/{id}")
    public ResponseEntity<?> getDistrictDetailsById(@PathVariable Integer id) {
        return mdmService.getDistrictDetailsById(id);
    }
    
    @Operation(summary = "End-point to get Constituency Details by giving District")
    @GetMapping("/getConstituencyDetailsByDistrictCode/{districtCode}")
    public ResponseEntity<?> getConstituencyDetailsByDistrictCode(@PathVariable Integer districtCode) {
        return mdmService.getConstituencyDetailsByDistrictCode(districtCode);
    }
    
    @GetMapping("/getAllConstituencyDetails")
    public ResponseEntity<?> getAllConstituencyDetails() {
        return mdmService.getAllConstituencyDetails();
    }
}
