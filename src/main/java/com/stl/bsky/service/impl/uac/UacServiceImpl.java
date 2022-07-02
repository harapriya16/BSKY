package com.stl.bsky.service.impl.uac;

import com.stl.bsky.common.*;
import com.stl.bsky.entity.uac.ResourceMaster;
import com.stl.bsky.entity.uac.RoleMaster;
import com.stl.bsky.entity.uac.StatusMaster;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.mapper.UacMapper;
import com.stl.bsky.model.uac.*;
import com.stl.bsky.repository.uac.ResourceRepository;
import com.stl.bsky.repository.uac.RoleRepository;
import com.stl.bsky.repository.uac.StatusRepository;
import com.stl.bsky.repository.uac.UserRepository;
import com.stl.bsky.service.CommonService;
import com.stl.bsky.service.SyncEntityIdCreationService;
import com.stl.bsky.service.uac.UacService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UacServiceImpl implements UacService {

    @Autowired
    CommonService commonService;
    @Autowired
    SyncEntityIdCreationService syncEntityIdCreationService;

    @Autowired
    StatusRepository statusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    UacMapper uacMapper;

    @Value("${build.base.path}")
    private String mediaBasePath;

    @Override
    public ResponseEntity<Object> getAllUsers(PagingAndSearching pagination) {
        UserListResponseDTO response = new UserListResponseDTO();
        Page<UserMaster> pageableUsers = null;

        log.info("Status of Pageable Pagination Required: " + pagination.getIsPaginationRequired()
                + " , Searching Required:  " + pagination.getIsSearchRequired() + " Order Required: "
                + pagination.getIsOrderRequired());

        Pageable pageable = commonService.getPageAndSort(pagination);
        if (pagination.getIsSearchRequired()) {
            GenericSpecification<UserMaster> specification = new GenericSpecification<>();
            specification.searchWith(pagination);
            pageableUsers = userRepository.findAll(specification, pageable);
        } else {
            pageableUsers = userRepository.findAll(pageable);
        }
        response.setUserListResponse(pageableUsers.getContent().stream().peek(userMaster -> {
            if (userMaster.getProfilePic() != null) {
                try {
                    byte[] array = Files.readAllBytes(Paths.get(mediaBasePath + userMaster.getProfilePic()));
                    String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(array));
                    userMaster.setProfilePic("data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(array));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).collect(Collectors.toList()));
        response.setTotalElement(pageableUsers.getTotalElements());
        response.setTotalPages(pageableUsers.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> createOrUpdateUser(UserMaster userMaster, Authentication authentication) {
        // fetching active status object
        StatusMaster status = statusRepository.findByStatusId(userMaster.getStatus().getStatusId());
        if (null == status) {
            return new ResponseEntity<>(new StatusResponse(0, "Invalid Status"), HttpStatus.NOT_ACCEPTABLE);
        }
        Long userId = userMaster.getId();
        RoleMaster role = new RoleMaster();
        List<UserMaster> users;
        if (!userMaster.getEmailId().isEmpty()) {
            if (null == userId || 0 == userId)
                users = userRepository.findByEmailIdAndStatusStatusId(userMaster.getEmailId(), status.getStatusId());
            else
                users = userRepository.findByEmailIdAndStatusStatusIdAndIdNot(
                        userMaster.getEmailId(), status.getStatusId(), userMaster.getId());
            if (users.size() > 0) {
                return new ResponseEntity<>(new StatusResponse(0, "Email Id already Exist"), HttpStatus.NOT_ACCEPTABLE);
            }
        }
        if (!userMaster.getContactNo().isEmpty()) {
            if (null == userId || 0 == userId)
                users = userRepository.findByContactNoAndStatusStatusId(
                        userMaster.getContactNo(), status.getStatusId());
            else
                users = userRepository.findByContactNoAndStatusStatusIdAndIdNot(
                        userMaster.getContactNo(), status.getStatusId(), userMaster.getId());

            if (users.size() > 0) {
                return new ResponseEntity<>(new StatusResponse(0, "MobileNo already Exist"), HttpStatus.NOT_ACCEPTABLE);
            }
        }
//        userMaster.setChangePwdFlag(false);
        userMaster.setStatus(status);
        userMaster.setCreatedBy(authentication.getName());
        userMaster.setUpdatedBy(authentication.getName());
        role.setRoleId(userMaster.getRole().getRoleId());
        userMaster.setRole(role);

        // to save user image in file system
        ResponseEntity<Object> resp = checkAndSaveProfilePic(userMaster.getProfilePic(), userMaster);
        if(resp.getStatusCode() != HttpStatus.OK) {
            return resp;
        }
        try {
            userRepository.save(userMaster);
            return new ResponseEntity<>(new StatusResponse(1, "User created successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse(0, "User already exists or some exception occurred"),
                    HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public ResponseEntity<Object> checkAndSaveProfilePic(String profilePic, UserMaster user) {
        String fullDirectory;
        String directory;
        if (profilePic != null) {
            if (!profilePic.isEmpty()) {
                String[] strings = profilePic.split(",");
                String extension;
                switch (strings[0]) {//check image's extension
                    case "data:image/jpeg;base64":
                        extension = "jpeg";
                        break;
                    case "data:image/png;base64":
                        extension = "png";
                        break;
                    case "data:image/jpg;base64":
                        extension = "jpg";
                        break;
                    default:
                        extension = "not image";
                        break;
                }
                if ("not image".equals(extension)) {
                    log.info("File Format is not correct.");
                    return new ResponseEntity<>(new StatusResponse(0, "Only image files are allowed"), HttpStatus.NOT_ACCEPTABLE);
                }
                System.out.println("Extension: " + extension);
                byte[] bytesArray = DatatypeConverter.parseBase64Binary(strings[1]);
                boolean isValidFile = CommonUtil.isValidMagicType(bytesArray);
                if (!isValidFile) {
                    log.info("File Format is not correct.");
                    return new ResponseEntity<>(new StatusResponse(0, "Only image files are allowed"), HttpStatus.NOT_ACCEPTABLE);
                }
                if ((bytesArray.length / 1024) >= 1024 && (bytesArray.length / 1024) <= 10) {
                    log.info("Image Must Be of Less than 1mb and Greater than 1 kb");
                    return new ResponseEntity<>(new StatusResponse(0, "Image Must Be of Less than 1mb and Greater than 1 kb."),
                            HttpStatus.NOT_ACCEPTABLE);
                }
                directory = File.separator + "userProfile" + File.separator;
                fullDirectory = mediaBasePath + directory;
                log.info("Image file path " + fullDirectory);
                String fileName = user.getUserName() + "_" + "profile_pic_" + System.currentTimeMillis() + "." + extension;
                try {
                    CommonUtil.saveFileInPhysicalPath(bytesArray, fullDirectory, fileName);
                    user.setProfilePic(directory + fileName);
                    return ResponseEntity.ok(1);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(new StatusResponse(0, "Invalid file"), HttpStatus.NOT_ACCEPTABLE);
                }
            }
        }
        return ResponseEntity.ok("No image provided");
    }

/*    @Override
    public ResponseEntity<Object> editUser(UserMaster userMaster, Authentication authentication) {
        UserMaster user = userRepository.findByUserName(userMaster.getUserName());
        RoleMaster role = new RoleMaster();
        if (null == user) {
            return new ResponseEntity<>(new StatusResponse(0, "No user found for this given username"), HttpStatus.BAD_REQUEST);
        }

        StatusMaster status = statusRepository.findByStatusId(userMaster.getStatus().getStatusId());
        if (null == status) {
            return new ResponseEntity<>(new StatusResponse(0, "Invalid Status"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (!request.getEmailId().isEmpty()) {
            if (null == user.getEmailId())
                user.setEmailId(request.getEmailId());
            else {
                if (!user.getEmailId().equalsIgnoreCase(request.getEmailId())) {
                    List<UserMaster> users = userRepository.findByEmailIdAndStatusStatusId(request.getEmailId(), 1);
                    if (users.size() > 0) {
                        return new ResponseEntity<>(new StatusResponse(0, "Email Id already Exist"),
                                HttpStatus.NOT_ACCEPTABLE);
                    } else {
                        user.setEmailId(request.getEmailId());
                    }
                }
            }
        }

        if (!request.getMobileNo().isEmpty()) {
            if (null == user.getContactNo())
                user.setContactNo(request.getMobileNo());
            else {
                if (!user.getContactNo().equalsIgnoreCase(request.getMobileNo())) {
                    List<UserMaster> users = userRepository.findByContactNoAndStatusStatusId(request.getMobileNo(), 1);
                    if (users.size() > 0) {
                        return new ResponseEntity<>(new StatusResponse(0, "MobileNo already Exist"),
                                HttpStatus.NOT_ACCEPTABLE);
                    } else {
                        user.setContactNo(request.getMobileNo());
                    }
                }
            }
        }
        user.setTitle(StringUtils.isEmpty(request.getTitle()) ? "NA" : request.getTitle());
        user.setFirstName(StringUtils.isEmpty(request.getFirstName()) ? "NA" : request.getFirstName());
        user.setLastName(StringUtils.isEmpty(request.getLastName()) ? "NA" : request.getLastName());
        role.setRoleId(request.getRoleId());
        user.setRole(role);
        user.setUpdatedBy(authentication.getName());
        user.setUpdatedDt(new Date());
        user.setStatus(status);
        // to save user image in file system
        ResponseEntity<Object> resp = checkAndSaveProfilePic(userMaster.getProfilePic(), userMaster);
        if(resp.getStatusCode() != HttpStatus.OK) {
            return resp;
        }
        try {
            userRepository.save(userMaster);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse(0, "Failed to update User details"),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(new StatusResponse(1, "User Updated Successfully"), HttpStatus.OK);
    }*/

    @Override
    public ResponseEntity<Object> addOrUpdateRole(RoleMaster roleMaster, Authentication authentication) {
        StatusMaster status = statusRepository.findByStatusId(roleMaster.getStatus().getStatusId());
        if (null == status) {
            return new ResponseEntity<>(new StatusResponse(0, "Invalid Status"), HttpStatus.OK);
        }

        roleMaster.setRoleName(roleMaster.getRoleName().trim().toUpperCase());
        roleMaster.setRoleCode(roleMaster.getRoleCode().trim().toUpperCase());
        roleMaster.setCreatedBy(authentication.getName());
        roleMaster.setUpdatedBy(authentication.getName());
        roleMaster.setStatus(status);
        try {
            if (null == roleMaster.getRoleId() || 0 == roleMaster.getRoleId())
                syncEntityIdCreationService.saveRole(roleMaster);
            else
                roleRepository.save(roleMaster);
            return new ResponseEntity<>(new StatusResponse(1, "Role added Successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse(0, "Some exception occurred"),
                    HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public ResponseEntity<Object> getAllRoles(PagingAndSearching pagination) {
        RoleResponse response = new RoleResponse();
        Page<RoleMaster> pageableRoles;

        log.info("Status of Pageable Pagination Required: " + pagination.getIsPaginationRequired()
                + " , Searching Required:  " + pagination.getIsSearchRequired() + " Order Required: "
                + pagination.getIsOrderRequired());

        Pageable pageable = commonService.getPageAndSort(pagination);
        if (pagination.getIsSearchRequired()) {
            GenericSpecification<RoleMaster> specification = new GenericSpecification<>();
            specification.searchWith(pagination);
            pageableRoles = roleRepository.findAll(specification, pageable);
        } else {
            pageableRoles = roleRepository.findAll(pageable);
        }
        response.setRoleResponse(pageableRoles.stream().collect(Collectors.toList()));
        response.setTotalElement(pageableRoles.getTotalElements());
        response.setTotalPages(pageableRoles.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

/*    @Override
    public ResponseEntity<Object> editRole(RoleMaster roleMaster, Authentication authentication) {
        StatusMaster status = statusRepository.findByStatusId(roleMaster.getStatus().getStatusId());
        if (null == status) {
            log.info("Status Does Not Exist!(404)");
            return new ResponseEntity<>(new StatusResponse(0, "Invalid Status"),
                    HttpStatus.NOT_FOUND);
        }
            roleMaster.setRoleName(roleMaster.getRoleName().trim().toUpperCase());
            roleMaster.setRoleCode(roleMaster.getRoleCode().trim().toUpperCase());
            roleMaster.setStatus(status);
            roleMaster.setUpdatedBy(authentication.getName());
            try {
                roleRepository.save(roleMaster);
            } catch (Exception e) {
                return new ResponseEntity<>(new StatusResponse(0, "Failed to update Role details"),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        return new ResponseEntity<>(new StatusResponse(1, "Role Updated Successfully"), HttpStatus.OK);
    }*/

/*    @Override
    public ResponseEntity<Object> addResource(AddResourceRequest request, Authentication authentication) {
        List<ResourceMaster> resourceMasterList;
        try {
//			User user = (User) authentication.getPrincipal();
            // Used for set resource
            StatusMaster status = statusRepository.findByStatusId(request.getStatus().getStatusId());
//            ResourceMaster resource = uacMapper.convertResourceDTOToResourceMaster(resourceMaster);
            request.setCreatedBy(authentication.getName());
            request.setUpdatedBy(authentication.getName());
            if (request.getParentResource() != null) {
                Long parentResourceId = request.getParentResource().getResourceId();
                if (parentResourceId != null && parentResourceId != 0) {
                    Optional<ResourceMaster> optionalResource = resourceRepository.findById(parentResourceId);
                    if (!optionalResource.isPresent()) {
                        return new ResponseEntity<>(new StatusResponse(0, "No such parent Id found"), HttpStatus.NOT_FOUND);
                    } else {
                        request.setParentResource(optionalResource.get());
                    }
                }
            }
            if (null == status) {
                return new ResponseEntity<>(new StatusResponse(0, "No such status found"), HttpStatus.NOT_FOUND);
            } else {
                request.setStatus(status);
            }
            if (null == request.getResourceId() || 0 == request.getResourceId()) {
//                resourceMasterList = resourceRepository.findAllByParentResourceAndOrderInEqualsAndStatusStatusId(
//                        resourceMaster.getParentResource(), resourceMaster.getOrderIn(), 1);
//                if (null != resourceMasterList && !resourceMasterList.isEmpty()) {
//                    return new ResponseEntity<>(new StatusResponse(0, "A resource already exists with same order and parent resource"),
//                            HttpStatus.NOT_ACCEPTABLE);
//                }
                syncEntityIdCreationService.saveResource(request);
            }
            else {
//                resourceMasterList = resourceRepository.findAllByParentResourceAndOrderInEqualsAndStatusStatusIdAndResourceIdNot(
//                        resourceMaster.getParentResource(), resourceMaster.getOrderIn(), 1, resourceMaster.getResourceId());
//                if (null != resourceMasterList && !resourceMasterList.isEmpty()) {
//                    return new ResponseEntity<>(new StatusResponse(0, "A resource already exists with same order and parent resource"),
//                            HttpStatus.NOT_ACCEPTABLE);
//                }
                resourceRepository.save(request);
            }
            return new ResponseEntity<>(new StatusResponse(1, "Resource added successfully"), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getCause().getCause().getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new StatusResponse(0, "Resource already exists or some exception occurred"),
                    HttpStatus.NOT_ACCEPTABLE);
        }
    }*/

    @Override
    public ResponseEntity<Object> addResource(AddResourceRequest request, Authentication authentication) {
        try {
//			User user = (User) authentication.getPrincipal();
            // Used for set resource
            ResourceMaster resource = uacMapper.convertResourceDTOToResourceMaster(request);
            StatusMaster status = statusRepository.findByStatusId(request.getStatus().getStatusId());
            if (null == status) {
                return new ResponseEntity<>(new StatusResponse(0, "No such status found"), HttpStatus.NOT_FOUND);
            } else {
                resource.setStatus(status);
            }
            resource.setCreatedBy(authentication.getName());
            resource.setUpdatedBy(authentication.getName());
            if (request.getParentResourceId() != null && request.getParentResourceId() != 0) {
                Optional<ResourceMaster> optionalResource = resourceRepository.findById(request.getParentResourceId());
                if (!optionalResource.isPresent()) {
                    return new ResponseEntity<>(new StatusResponse(0, "No such parent Id found"), HttpStatus.NOT_FOUND);
                } else {
                    resource.setParentResource(optionalResource.get());
                }
            }
           
            if (null == resource.getResourceId() || 0 == resource.getResourceId())
                syncEntityIdCreationService.saveResource(resource);
            else
                resourceRepository.save(resource);
            return new ResponseEntity<>(new StatusResponse(1, "Resource added successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new StatusResponse(0, "Resource already exists or some exception occurred"),
                    HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public ResponseEntity<Object> getAllResource(String flatOrNested) {
        if (null == flatOrNested) {
            return new ResponseEntity<>(new StatusResponse(0, "Please mention if flat or nested"), HttpStatus.NOT_ACCEPTABLE);
        }
        List<ResourceResponseDTO> responseDTOList = new ArrayList<>();
        if ("flat".equalsIgnoreCase(flatOrNested)) {
            List<ResourceMaster> resourceMasterList = resourceRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
            resourceMasterList.forEach(resourceMaster -> {
                ResourceResponseDTO responseDTO = uacMapper.convertResourceMasterToResourceResponseDTO(resourceMaster);
                if (!(null == resourceMaster.getParentResource()))
                    responseDTO.setParentResourceId(resourceMaster.getParentResource().getResourceId());
                responseDTOList.add(responseDTO);
            });
        } else if ("nested".equalsIgnoreCase(flatOrNested)) {
            List<ResourceMaster> resourceMasterList = resourceRepository.findAllByParentResourceResourceIdOrderByOrderIn(null);
            resourceMasterList.forEach(resourceMaster -> {
                ResourceResponseDTO responseDTO = uacMapper.convertResourceMasterToResourceResponseDTO(resourceMaster);
                if (responseDTO.isHasSubMenu()) {
                    responseDTO.setResponseDTOs(getChildResources(resourceMaster.getResourceId()));
                }
                responseDTOList.add(responseDTO);
            });
        } else {
            return new ResponseEntity<>(new StatusResponse(0, "Please mention if flat or nested"), HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(responseDTOList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getAllResourcesByRoleId(int roleId) {
        RoleMaster roleMaster = roleRepository.findByRoleId(roleId);
        if (null == roleMaster) {
            return new ResponseEntity<>(new StatusResponse(0, "No Such Role Found"), HttpStatus.OK);
        }
        List<ResourceResponseDTO> responseDTOList = getResponseDTOListByRole(roleMaster);
        return new ResponseEntity<>(responseDTOList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getAllResourcesByAuthenticatingRole(HttpServletRequest request) {
        //UserMaster user = userRepository.findByUserName(authentication.getName());
//        UserMaster user = userRepository.findByUserName("orgadmin");
    	UserMaster user = commonService.extractUserFromToken(request);
    	RoleMaster role = roleRepository.findByRoleId(user.getRole().getRoleId());
        List<ResourceResponseDTO> responseDTOList = getResponseDTOListByRole(role);
        return new ResponseEntity<>(responseDTOList, HttpStatus.OK);
    }

    public List<ResourceResponseDTO> getResponseDTOListByRole(RoleMaster roleMaster) {
    	System.out.println("roleMaster"+roleMaster.getResources().size());
        List<ResourceResponseDTO> responseDTOList = new ArrayList<>();
        List<ResourceMaster> resourceMasterList = roleMaster.getResources();
        List<ResourceMaster> filteredList = resourceMasterList.stream().filter(
                resourceMaster -> resourceMaster.getParentResource() == null
        ).sorted(Comparator.comparing(ResourceMaster::getOrderIn)).collect(Collectors.toList());
        filteredList.forEach(resource -> {
            ResourceResponseDTO responseDTO = uacMapper.convertResourceMasterToResourceResponseDTO(resource);
            responseDTO.setHasSubMenu(resource.getHasSubMenu());
            if (responseDTO.isHasSubMenu()) {
                responseDTO.setResponseDTOs(getChildResourcesByRole(resource.getResourceId(),
                        resourceMasterList.stream().filter(resourceMaster -> resourceMaster.getParentResource() != null)
                                .collect(Collectors.toList())));
            }
            responseDTOList.add(responseDTO);
        });
        return responseDTOList;
    }

    @Override
    public ResponseEntity<Object> getMappedAndAllResourceDetails(int roleId) {
        List<ResourceResponseDTO> responseDTOList = new ArrayList<>();
        List<ResourceMaster> resourceMasterList = resourceRepository.findAllByParentResourceResourceIdOrderByOrderIn(null);
        resourceMasterList.forEach(resourceMaster -> {
            ResourceResponseDTO responseDTO = uacMapper.convertResourceMasterToResourceResponseDTO(resourceMaster);
            List<RoleMaster> roleMasterList = resourceMaster.getRoles();
            if (!roleMasterList.isEmpty()) {
                roleMasterList.forEach(role -> {
                    if (role.getRoleId() == roleId) {
                        responseDTO.setMapped(true);
                    }
                });
            }
            if (responseDTO.isHasSubMenu()) {
                responseDTO.setResponseDTOs(getChildMappedAndAllResources(resourceMaster.getResourceId(), roleId));
            }
            responseDTOList.add(responseDTO);
        });
        return new ResponseEntity<>(responseDTOList, HttpStatus.OK);
    }

    public List<ResourceResponseDTO> getChildResources(Long id) {
        List<ResourceMaster> resourceMasterList = resourceRepository.findAllByParentResourceResourceIdOrderByOrderIn(id);
        List<ResourceResponseDTO> responseDTOList = new ArrayList<>();
        resourceMasterList.forEach(resource -> {
            ResourceResponseDTO responseDTO = uacMapper.convertResourceMasterToResourceResponseDTO(resource);
            responseDTO.setParentResourceId(resource.getParentResource().getResourceId());
            if (responseDTO.isHasSubMenu()) {
                responseDTO.setResponseDTOs(getChildResources(resource.getResourceId()));
            }
            responseDTOList.add(responseDTO);
        });
        return responseDTOList;
    }

    public List<ResourceResponseDTO> getChildResourcesByRole(Long id, List<ResourceMaster> resourceMasterList) {
        List<ResourceMaster> filteredList = resourceMasterList.stream().filter(
                resourceMaster -> resourceMaster.getParentResource().getResourceId() == id
        ).sorted(Comparator.comparing(ResourceMaster::getOrderIn)).collect(Collectors.toList());
        List<ResourceResponseDTO> responseDTOList = new ArrayList<>();
        filteredList.forEach(resource -> {
            ResourceResponseDTO responseDTO = uacMapper.convertResourceMasterToResourceResponseDTO(resource);
            responseDTO.setParentResourceId(resource.getParentResource().getResourceId());
            if (responseDTO.isHasSubMenu()) {
                responseDTO.setResponseDTOs(getChildResourcesByRole(resource.getResourceId(), resourceMasterList));
            }
            responseDTOList.add(responseDTO);
        });
        return responseDTOList;
    }

    public List<ResourceResponseDTO> getChildMappedAndAllResources(Long id, int roleId) {
        List<ResourceMaster> resourceMasterList = resourceRepository.findAllByParentResourceResourceIdOrderByOrderIn(id);
        List<ResourceResponseDTO> responseDTOList = new ArrayList<>();
        resourceMasterList.forEach(resource -> {
            ResourceResponseDTO responseDTO = uacMapper.convertResourceMasterToResourceResponseDTO(resource);
            responseDTO.setParentResourceId(resource.getParentResource().getResourceId());
            List<RoleMaster> roleMasterList = resource.getRoles();
            if (!roleMasterList.isEmpty()) {
                roleMasterList.forEach(role -> {
                    if (role.getRoleId() == roleId) {
                        responseDTO.setMapped(true);
                    }
                });
            }
            if (responseDTO.isHasSubMenu()) {
                responseDTO.setResponseDTOs(getChildMappedAndAllResources(resource.getResourceId(), roleId));
            }
            responseDTOList.add(responseDTO);
        });
        return responseDTOList;
    }

    @Override
    public ResponseEntity<Object> addRoleResource(EditRoleResourceRequest request, Authentication authentication) {
        try {
            RoleMaster role = roleRepository.findByRoleId(request.getRole_id());
            List<ResourceMaster> resourceMasterList = new ArrayList<>();

            for (ResourceInfoRequest resourceInfoRequest : request.getResource_info()) {
                ResourceMaster resourceMaster = resourceRepository.findByResourceId(resourceInfoRequest.getResource_id());
                if (null == resourceMaster)
                    return new ResponseEntity<>(new StatusResponse(0, "Resource id not found"), HttpStatus.NOT_FOUND);
                resourceMasterList.add(resourceMaster);
            }
            role.setResources(resourceMasterList);
            roleRepository.save(role);
            return new ResponseEntity<>(new StatusResponse(1, "Role Resource mapping Added Successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new StatusResponse(0, "Role-Resource Mapping Failed"),
                    HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public ResponseEntity<Object> addStatus(StatusMaster statusMaster, Authentication authentication) {
        statusMaster.setCreatedBy(authentication.getName());
        statusMaster.setUpdatedBy(authentication.getName());
        try {
            return new ResponseEntity<>(statusRepository.save(statusMaster), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse(0, "Unable to create new status"), HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<Object> getAllStatus() {
        List<StatusMaster> allStatus = statusRepository.findAll();
        return new ResponseEntity<>(allStatus, HttpStatus.OK);
    }
//    public ResponseEntity<Object> checkAndSaveIconImage(String iconImage, ResourceMaster user) {
//        String fullDirectory;
//        String directory;
//        if (iconImage != null) {
//            if (!iconImage.isEmpty()) {
//                String[] strings = iconImage.split(",");
//                String extension;
//                switch (strings[0]) {//check image's extension
//                    case "data:image/jpeg;base64":
//                        extension = "jpeg";
//                        break;
//                    case "data:image/png;base64":
//                        extension = "png";
//                        break;
//                    case "data:image/jpg;base64":
//                        extension = "jpg";
//                        break;
//                    default:
//                        extension = "not image";
//                        break;
//                }
//                if ("not image".equals(extension)) {
//                    log.info("File Format is not correct.");
//                    return new ResponseEntity<>(new StatusResponse(0, "Only image files are allowed"), HttpStatus.NOT_ACCEPTABLE);
//                }
//                System.out.println("Extension: " + extension);
//                byte[] bytesArray = DatatypeConverter.parseBase64Binary(strings[1]);
//                boolean isValidFile = CommonUtil.isValidMagicType(bytesArray);
//                if (!isValidFile) {
//                    log.info("File Format is not correct.");
//                    return new ResponseEntity<>(new StatusResponse(0, "Only image files are allowed"), HttpStatus.NOT_ACCEPTABLE);
//                }
//                if ((bytesArray.length / 1024) >= 1024 && (bytesArray.length / 1024) <= 10) {
//                    log.info("Image Must Be of Less than 1mb and Greater than 1 kb");
//                    return new ResponseEntity<>(new StatusResponse(0, "Image Must Be of Less than 1mb and Greater than 1 kb."),
//                            HttpStatus.NOT_ACCEPTABLE);
//                }
//                directory = File.separator + "userProfile" + File.separator;
//                fullDirectory = mediaBasePath + directory;
//                log.info("Image file path " + fullDirectory);
//                String fileName = user.getName() + "_" + "icon_img" + System.currentTimeMillis() + "." + extension;
//                try {
//                    CommonUtil.saveFileInPhysicalPath(bytesArray, fullDirectory, fileName);
//                    user.setIconImagePath(directory + fileName);
//                    return ResponseEntity.ok(1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return new ResponseEntity<>(new StatusResponse(0, "Invalid file"), HttpStatus.NOT_ACCEPTABLE);
//                }
//            }
//        }
//        return ResponseEntity.ok("No image provided");
//    }
	

}
