package com.stl.bsky.service.impl.mdm;

import com.stl.bsky.common.GenericSpecification;
import com.stl.bsky.common.PagingAndSearching;
import com.stl.bsky.common.StatusResponse;
import com.stl.bsky.entity.mdm.*;
import com.stl.bsky.entity.uac.StatusMaster;
import com.stl.bsky.mapper.MdmMapper;
import com.stl.bsky.model.mdm.GenCodeNameRepDto;
import com.stl.bsky.model.mdm.IconDetailsDTO;
import com.stl.bsky.model.mdm.IconResponse;
import com.stl.bsky.repository.mdm.*;
import com.stl.bsky.repository.uac.StatusRepository;
import com.stl.bsky.service.CommonService;
import com.stl.bsky.service.mdm.MdmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("mdmService")
@Slf4j
public class MdmServiceImpl implements MdmService {

    @Autowired
    StatusRepository statusRepository;
    @Autowired
    IconRepository iconRepository;
    @Autowired
    GenCodeRepository genCodeRepository;    
    @Autowired
    AccessControlTypeRepository accessControlTypeRepository;    
    @Autowired
    StateMasterRepository stateMasterRepository;
    @Autowired
    CityDetailsRepository cityDetailsRepository;
    @Autowired
    PostOfficeRepository postOfficeRepository;
    @Autowired
    DistrictMasterRepository districtMasterRepository;   
    @Autowired
    ResourceTypeRepository resourceTypeRepository;    
    
    @Autowired
    ConstituencyRespository constituencyRespository;
    @Autowired
    CommonService commonService;

    @Autowired
    MdmMapper mdmMapper;
    
    @Override
    public ResponseEntity<Object> createIcon(IconDetailsDTO iconDetailsDTO) {
       // StatusMaster statusMaster = statusRepository.findByStatusId(iconDetailsDTO.getStatusId());
        IconDetails iconDetailsEnt=mdmMapper.convertIconDetailsDTOToEntity(iconDetailsDTO);
        if(iconDetailsEnt.getIconType().equalsIgnoreCase("Image") && iconRepository.findAllByImageName(iconDetailsEnt.getImageName()).size()>0) {
        	return new ResponseEntity<>(new StatusResponse(409, "Image Name Already Exist"), HttpStatus.OK);
        }else if(iconDetailsEnt.getIconType().equalsIgnoreCase("FontAwesome") && iconRepository.findAllByFaText(iconDetailsEnt.getFaText()).size()>0) {
        	return new ResponseEntity<>(new StatusResponse(409, "FontAwesome Already Exist"), HttpStatus.OK);
        }
        
        try {
            iconRepository.save(iconDetailsEnt);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new StatusResponse(-1, "Unable to save"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new StatusResponse(0, "Icon Created Successfully"), HttpStatus.OK);
    }

    @Override
    public Object getAllIcons(PagingAndSearching pagination) {
        IconResponse responseList = new IconResponse();
        List<IconDetailsDTO> iconResponse = new ArrayList<>();

        Page<IconDetails> pageableIcons = null;
        List<IconDetails> icons = null;
        Pageable pageable = null;
        log.info("Status of Pageable Pagination Required: " + pagination.getIsPaginationRequired() + " , Searching Required:  " + pagination.getIsSearchRequired() + " Order Required: " + pagination.getIsOrderRequired());

        if (pagination.getIsPaginationRequired()) {
            try {
                pageable = commonService.setPageable(pagination);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pagination.getIsSearchRequired()) {
                if (pagination.getKey().equalsIgnoreCase("statusId")) {
                    log.info("Inside Paging and Searching");
                    //pageableIcons = iconRepository.findAllByStatusStatusId(Integer.parseInt(pagination.getValue()), pageable);
                    pageableIcons=iconRepository.findAllByIconType(pagination.getValue(),pageable);
                }
            } else {
                log.info("Inside Paging Only");
                pageableIcons = iconRepository.findAll(pageable);

            }
            iconResponse = mdmMapper.toIconDTOFromIconEntity(pageableIcons.getContent().stream());
            responseList.setTotalPages(pageableIcons.getTotalPages());
            responseList.setTotalElement(pageableIcons.getTotalElements());
        } else {
            if (pagination.getIsSearchRequired()) {
                log.info("Inside Searching Only");
                if (pagination.getKey().equalsIgnoreCase("statusId")) {
                    icons = pagination.getIsOrderRequired()
                            ? iconRepository.findAllByStatusStatusId(Integer.parseInt(pagination.getValue()),
                            Sort.by((pagination.getIsAscending() ? Sort.Direction.ASC
                                    : Sort.Direction.DESC)))
                            : iconRepository.findAllByStatusStatusId(Integer.parseInt(pagination.getValue()));
                }
                iconResponse = mdmMapper.toIconDTOFromIconEntity(icons.stream());
            } else {
                log.info("Inside Without Paging and Searching");
                icons = pagination.getIsOrderRequired() ?
                        iconRepository.findAll(Sort.by((pagination.getIsAscending() ? Sort.Direction.ASC : Sort.Direction.DESC), pagination.getOrderField())) :
                        iconRepository.findAll();
                iconResponse = mdmMapper.toIconDTOFromIconEntity(icons.stream());
            }
        }
        responseList.setIconDetailsDTOList(iconResponse);
        return responseList;
    }
 
    @Override
	public Object getAllIconsWithSpecification(PagingAndSearching pagination) {
		IconResponse iconResponse = new IconResponse();
		List<IconDetailsDTO> iconDetailsDTOList;
		Page<IconDetails> pageableIconDetails;

		log.info("Status of Pageable Pagination Required: " + pagination.getIsPaginationRequired()
				+ " , Searching Required:  " + pagination.getIsSearchRequired() + " Order Required: "
				+ pagination.getIsOrderRequired());

		

		Pageable pageable = commonService.getPageAndSort(pagination);
		if (pagination.getIsSearchRequired()) {
			GenericSpecification<IconDetails> specification = new GenericSpecification<>();
			specification.searchWith(pagination);
			pageableIconDetails = iconRepository.findAll(specification, pageable);
		} else {
			pageableIconDetails = iconRepository.findAll(pageable);
		}
		iconDetailsDTOList = mdmMapper.toIconDetailsDTOList(pageableIconDetails.stream());
		iconResponse.setTotalElement(pageableIconDetails.getTotalElements());
		iconResponse.setTotalPages(pageableIconDetails.getTotalPages());
		iconResponse.setIconDetailsDTOList(iconDetailsDTOList);
		return iconResponse;
	}
    /**
     * created by Sagen
     * GenCode inserting into the mdm
     */
    @Override
    public ResponseEntity<?> createGenCode(GenCodeEntity genCodeEntity) {

        Optional<GenCodeEntity> genCode = genCodeRepository.findByName(genCodeEntity.getName());
        if (genCode.isPresent()) {
            return new ResponseEntity<>(new StatusResponse(0, "Gen code is already exists!!"), HttpStatus.OK);
        }
        genCodeEntity.setCreatedBy("admin");
        genCodeEntity.setCreated_on(new Date());
        genCodeEntity.setIsActive(1);
        genCodeEntity.setIsDeleted(1);
        genCodeRepository.save(genCodeEntity);

        return ResponseEntity.ok(new StatusResponse(1, "Gen Code created successfully"));
    }

    @Override
    public ResponseEntity<?> getAllGenCodeDetails() {

        List<GenCodeEntity> genCodeNameList = genCodeRepository.findByParentIdAndIsActive(0, 1);

        if (genCodeNameList.isEmpty()) {
            return new ResponseEntity<>(new StatusResponse(0, "No records found!!"), HttpStatus.OK);
        }
        List<GenCodeNameRepDto> returnList = new ArrayList<>();
        genCodeNameList.forEach(v -> {
            GenCodeNameRepDto genCodeNameRepDto = new GenCodeNameRepDto();
            genCodeNameRepDto.setName(v.getName());
            genCodeNameRepDto.setParentId(v.getParentId());
            genCodeNameRepDto.setId(v.getId());
            genCodeNameRepDto.setChildLists(mdmMapper.convertGenEntityToDTO(genCodeRepository.findByParentIdAndIsActive(v.getId().intValue(), 1)));
            returnList.add(genCodeNameRepDto);
        });

        return ResponseEntity.ok(returnList);
    }

    @Override
    public ResponseEntity<?> getGenCodesByParentIdAndActive(int parentId, int isActive) {
        List<GenCodeEntity> genCodeEntityList = genCodeRepository.findByParentIdAndIsActive(parentId, isActive);

        if (genCodeEntityList.isEmpty()) {
            return new ResponseEntity<>(new StatusResponse(0, "No records found for this parent id : " + parentId), HttpStatus.OK);
        }
        List<GenCodeNameRepDto> returnList = new ArrayList<>();
        genCodeEntityList.forEach(v -> {
            GenCodeNameRepDto genCodeNameRepDto = new GenCodeNameRepDto();
            genCodeNameRepDto.setName(v.getName());
            genCodeNameRepDto.setParentId(v.getParentId());
            genCodeNameRepDto.setId(v.getId());
            returnList.add(genCodeNameRepDto);
        });

        return ResponseEntity.ok(returnList);
    }
  

    @Override
    public ResponseEntity<?> getAccessControlEntityTypeDetails() {
        List<AccessControlTypeEntity> accessControlTypeEntity = accessControlTypeRepository.findAll();
        return ResponseEntity.ok(accessControlTypeEntity);
    }

    @Override
    public ResponseEntity<?> getAccessControlEntityTypeDetailsById(Long id) {
        Optional<AccessControlTypeEntity> getAccessControlEntityTypeDetailsById = accessControlTypeRepository.findById(id);
        if(getAccessControlEntityTypeDetailsById.isPresent()) {
            return ResponseEntity.ok(new StatusResponse(1, "Record Found", getAccessControlEntityTypeDetailsById.get()));
            //return ResponseEntity.ok(getAccessControlEntityTypeDetailsById.get());
        }else {
            return new ResponseEntity<>(new StatusResponse(0, "No records found for this Access id : " + id), HttpStatus.NOT_FOUND);
            //return new ResponseEntity<>(new AccessControlTypeEntity(), HttpStatus.NOT_FOUND);
        }

    }

	@Override
	public ResponseEntity<?> getStateDetails() {
		List<StateMaster> getStateDetails = stateMasterRepository.findAll();
        return ResponseEntity.ok(getStateDetails);
	}

	@Override
	public ResponseEntity<?> getStateDetailsByStateCode(String stateCode) {
		Optional<StateMaster> getStateDetailsById = stateMasterRepository.findById(stateCode);
        //return ResponseEntity.ok(getStateDetailsById.get());
        //return new ResponseEntity<>(new StateDetailsEntity(), HttpStatus.NOT_FOUND);
        return getStateDetailsById.map(stateMaster -> ResponseEntity.ok(new StatusResponse(1, "Record Found", stateMaster)))
                .orElseGet(() -> new ResponseEntity<>(new StatusResponse(0, "No records found for this State Code : " + stateCode), HttpStatus.NOT_ACCEPTABLE));
	}

    @Override
    public ResponseEntity<?> getCityDetailsByStateCode(String stateCode) {
        List<CityDetailsEntity> cityDetails = cityDetailsRepository.findByStateMasterStateCode(stateCode);
        return ResponseEntity.ok(cityDetails);
    }
    @Override
	public ResponseEntity<?> getCityDetails() {
		List<CityDetailsEntity> getCityDetails = cityDetailsRepository.findAll();
        return ResponseEntity.ok(getCityDetails);
	}
	
	@Override
	public ResponseEntity<?> getCityDetailsById(Long id) {
		Optional<CityDetailsEntity> getCityDetailsById = cityDetailsRepository.findById(id);
        //return ResponseEntity.ok(getCityDetailsByStateCode.get());
        //return new ResponseEntity<>(new CityDetailsEntity(), HttpStatus.NOT_FOUND);
        return getCityDetailsById.map(cityDetailsEntity -> ResponseEntity.ok(new StatusResponse(1, "Record Found", cityDetailsEntity))).
                orElseGet(() -> new ResponseEntity<>(new StatusResponse(0, "No records found for this Id  : " + id),
                HttpStatus.BAD_REQUEST));
	}

	@Override
	public ResponseEntity<?> getPostOfficeDetails() {
		List<PostOfficeEntity> getPostOfficeDetails = postOfficeRepository.findAll();
        return ResponseEntity.ok(getPostOfficeDetails);
	}

	@Override
	public ResponseEntity<?> getPostOfficeDetailsByPinCode(Long pinCode) {
		Optional<PostOfficeEntity> getPostOfficeDetailsByPinCode = postOfficeRepository.findByPinCode(pinCode);
        if(getPostOfficeDetailsByPinCode.isPresent()) {
            return ResponseEntity.ok(new StatusResponse(1, "Record Found", getPostOfficeDetailsByPinCode.get()));
            //return ResponseEntity.ok(getPostOfficeDetailsByPinCode.get());
        }else {
            return new ResponseEntity<>(new StatusResponse(0, "No records found for this Pin Code : " + pinCode), HttpStatus.OK);
            //return new ResponseEntity<>(new PostOfficeEntity(), HttpStatus.NOT_FOUND);
        }
	}

	@Override
	public ResponseEntity<?> getDistrictDetails() {
		List<DistrictMaster> getDistrictDetails = districtMasterRepository.findAll();
        return ResponseEntity.ok(getDistrictDetails);
	}

	@Override
	public ResponseEntity<?> getDistrictDetailsById(Integer id) {
		Optional<DistrictMaster> getDistrictDetailsById = districtMasterRepository.findById(id);
        //return ResponseEntity.ok(getDistrictDetailsById.get());
        //return new ResponseEntity<>(new DistrictDetailsEntity(), HttpStatus.NOT_FOUND);
        return getDistrictDetailsById.map(districtMaster -> ResponseEntity.ok(new StatusResponse(1, "Record Found", districtMaster))).orElseGet(() -> new ResponseEntity<>(new StatusResponse(0, "No records found for this district id : " + id), HttpStatus.NOT_ACCEPTABLE));
	}

	@Override
	public ResponseEntity<?> getConstituencyDetailsByDistrictCode(Integer districtCode) {
		Optional<List<Constituency>> getConstituencyDetailsByDistrictCode = constituencyRespository.findByDistrictMasterDistrictCode(districtCode);
        if(getConstituencyDetailsByDistrictCode.isPresent()) {
            return ResponseEntity.ok(new StatusResponse(1, "Record Found", getConstituencyDetailsByDistrictCode.get()));
        }else {
            return new ResponseEntity<>(new StatusResponse(0, "No records found for this district id : " + districtCode), HttpStatus.OK);
        }
	}

	@Override
	public ResponseEntity<?> getAllConstituencyDetails() {
		List<Constituency> constituencyDetails = constituencyRespository.findAll();
		return ResponseEntity.ok(constituencyDetails);
	}

}
