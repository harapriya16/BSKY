package com.stl.bsky.service.mdm;

import com.stl.bsky.common.PagingAndSearching;
import com.stl.bsky.entity.mdm.GenCodeEntity;
import com.stl.bsky.model.mdm.IconDetailsDTO;

import org.springframework.http.ResponseEntity;

public interface MdmService {

    ResponseEntity<Object> createIcon(IconDetailsDTO iconDetailsDTO);

    Object getAllIcons(PagingAndSearching pagination);

    ResponseEntity<?> createGenCode(GenCodeEntity genCodeEntity);

    ResponseEntity<?> getAllGenCodeDetails();

    ResponseEntity<?> getGenCodesByParentIdAndActive(int parentId, int isActive);

    ResponseEntity<?> getAccessControlEntityTypeDetails();

    ResponseEntity<?> getAccessControlEntityTypeDetailsById(Long id);

    ResponseEntity<?> getStateDetails();

    ResponseEntity<?> getCityDetailsById(Long id);

    ResponseEntity<?> getCityDetails();

    ResponseEntity<?> getPostOfficeDetails();

    ResponseEntity<?> getPostOfficeDetailsByPinCode(Long pinCode);

    ResponseEntity<?> getDistrictDetails();

    ResponseEntity<?> getDistrictDetailsById(Integer id);

    ResponseEntity<?> getStateDetailsByStateCode(String stateCode);

    ResponseEntity<?> getCityDetailsByStateCode(String stateCode);

	ResponseEntity<?> getConstituencyDetailsByDistrictCode(Integer districtCode);

	ResponseEntity<?> getAllConstituencyDetails();

	Object getAllIconsWithSpecification(PagingAndSearching pagination);
}
