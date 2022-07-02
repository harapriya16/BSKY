package com.stl.bsky.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stl.bsky.common.CommonUtil;
import com.stl.bsky.common.ImageUpload;
import com.stl.bsky.common.PagingAndSearching;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.exception.ApiException;
import com.stl.bsky.exception.InvalidFileException;
import com.stl.bsky.repository.uac.UserRepository;
import com.stl.bsky.service.CommonService;
import com.stl.bsky.utility.JwtUtil;
//import com.stl.nta.entity.registration.AttachmentEntity;
//import com.stl.nta.repository.registration.AttachmentEntityRepository;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

	private static long maxUploadSize=2097152 ;
    @Value("${build.base.path}")
    private String mediaBasePath;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Override
    public Pageable setPageable(PagingAndSearching pagination) {
        Pageable pageable = null;
        if (pagination.getIsPaginationRequired()) {
            pageable= pagination.getIsOrderRequired() ?
                    PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), (pagination.getIsAscending() ? Sort.Direction.ASC : Sort.Direction.DESC), pagination.getOrderField()) :
                    PageRequest.of(pagination.getPageNumber(), pagination.getPageSize());

        }
        return pageable;
    }

    @Override
    public Pageable getPageAndSort(PagingAndSearching pagination) {
        log.info("Inside the common service");
        Pageable pageable;
        if (pagination.getIsPaginationRequired()) {
            if (pagination.getIsOrderRequired()) {
                pageable = PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), pagination.getIsAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, pagination.getOrderField());
            } else {
                pageable = PageRequest.of(pagination.getPageNumber(), pagination.getPageSize());
            }
        } else {
            if (pagination.getIsOrderRequired()) {
                pageable = PageRequest.of(0, Integer.MAX_VALUE, pagination.getIsAscending() ? Sort.Direction.ASC : Sort.Direction.DESC, pagination.getOrderField());
            } else {
                pageable = PageRequest.of(0, Integer.MAX_VALUE);
            }
        }
        return pageable;
    }

   /* @Override
    public void checkAndSaveAttachment(ImageUpload upload, Long refId, Long tabRefId, Long docRefId) {
        String fullDirectory;
        String directory;
        String attachment;
        if(upload != null) {
            attachment = upload.getImage();
            if (attachment != null) {
                if (!attachment.isEmpty()) {
                    String[] strings = attachment.split(",");
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
                        case "data:application/pdf;base64":
                            extension = "pdf";
                            break;
                        default:
                            extension = "invalid file";
                            break;
                    }
                    if ("invalid file".equals(extension)) {
                        log.info("File Format is not correct.");
                        throw new InvalidFileException("Only image and pdf files are allowed");
                    }
                    System.out.println("Extension: " + extension);
                    byte[] bytesArray = DatatypeConverter.parseBase64Binary(strings[1]);
                    boolean isValidFile = CommonUtil.isValidMagicType(bytesArray);
                    if (!isValidFile) {
                        log.info("File Format is not correct.");
                        throw new InvalidFileException("Only image and pdf files are allowed");
                    }
                /*if ((bytesArray.length / 1024) >= 1024 && (bytesArray.length / 1024) <= 10) {
                    log.info("Image Must Be of Less than 1mb and Greater than 1 kb ");
                    throw new InvalidFileException("Image Must Be of Less than 1mb and Greater than 1 kb.");
                }*/
                   /* directory = File.separator + "userAttachment" + File.separator;
                    fullDirectory = mediaBasePath + directory;
                    log.info("Attachment file path " + fullDirectory);
                    String fileName = System.currentTimeMillis() + "." + extension;
                    try {
                        saveFileInPhysicalPath(bytesArray, fullDirectory, fileName);
                        AttachmentEntity attachmentEntity;
                        Optional<AttachmentEntity> optional = attachmentRepository.findByRefIdAndTabTypeIdAndDocTypeId(refId,
                                tabRefId, docRefId);
                        if (optional.isPresent()) {
                            attachmentEntity = optional.get();
                            File file = new File(mediaBasePath + attachmentEntity.getDocPath());
                            if (file.delete()) {
                                System.out.println("File deleted");
                            } else {
                                System.out.println("Unable to delete file");
                            }
                        } else {
                            attachmentEntity = new AttachmentEntity();
                            attachmentEntity.setRefId(refId);
                            attachmentEntity.setTabTypeId(tabRefId);
                            attachmentEntity.setDocTypeId(docRefId);
                        }
                        attachmentEntity.setOriginalName(upload.getImageName());
                        attachmentEntity.setDocPath(directory + fileName);
                        attachmentEntity.setDocFormat(extension);
                        attachmentRepository.save(attachmentEntity);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new InvalidFileException("Invalid file", e.getCause());
                    }
                }
            }
        }
    }*/
    
    @Override
    public String checkAndSaveAttachmentInUserMaster(String image) {
        String fullDirectory;
        String directory;
        String fileName ="";
        if(image != null) {
            
            if (image != null) {
                if (!image.isEmpty()) {
                    String[] strings = image.split(",");
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
                            extension = "invalid file";
                            break;
                    }
                    if ("invalid file".equals(extension)) {
                        log.info("File Format is not correct.");
                        throw new InvalidFileException("Only image files are allowed");
                    }
                    System.out.println("Extension: " + extension);
                    byte[] bytesArray = DatatypeConverter.parseBase64Binary(strings[1]);
                    boolean isValidFile = CommonUtil.isValidMagicType(bytesArray);
                    if (!isValidFile) {
                        log.info("File Format is not correct.");
                        throw new InvalidFileException("Only image files are allowed");
                    }
                /*if ((bytesArray.length / 1024) >= 1024 && (bytesArray.length / 1024) <= 10) {
                    log.info("Image Must Be of Less than 1mb and Greater than 1 kb ");
                    throw new InvalidFileException("Image Must Be of Less than 1mb and Greater than 1 kb.");
                }*/
                    directory = File.separator + "userProfile" + File.separator;
                    fullDirectory = mediaBasePath + directory;
                    log.info("Attachment file path " + fullDirectory);
                    fileName = "user_profile_"+System.currentTimeMillis() + "." + extension;
                    try {
                        saveFileInPhysicalPath(bytesArray, fullDirectory, fileName);                                              
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new InvalidFileException("Invalid file", e.getCause());
                    }
                }
            }
        }
        return fileName;
    }   
    public static void saveFileInPhysicalPath(byte[] bytesArray, String directory, String fileName) throws IOException {
        Path path;
        File dir = new File(directory);
        if (!dir.exists())
            dir.mkdirs();
        path = Paths.get(directory + fileName);
        Files.write(path, bytesArray);
        log.info("Saved File Path : " + path);
    }

    /* @Override
    public ImageUpload getFileFromPhysicalPath(Long refId, Long tabRefId, Long docRefId) throws IOException {
        Optional<AttachmentEntity> attachmentEntity = attachmentRepository.findByRefIdAndTabTypeIdAndDocTypeId(refId,
                tabRefId, docRefId);
        ImageUpload imageUpload = new ImageUpload();
        if(attachmentEntity.isPresent()) {
            byte[] array = Files.readAllBytes(Paths.get(mediaBasePath + attachmentEntity.get().getDocPath()));
            String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(array));
            if(attachmentEntity.get().getDocFormat().equals("pdf")) {
                imageUpload.setImage("data:application/pdf;base64," + Base64.getEncoder().encodeToString(array));
            } else
                imageUpload.setImage("data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(array));
            imageUpload.setImageName(attachmentEntity.get().getOriginalName());
        }
        return imageUpload;
    }*/

    @Override
    public UserMaster extractUserFromToken(HttpServletRequest request) {
        String token = null;
       UserMaster userMaster =new UserMaster();
        final String authorization = request.getHeader("Authorization");
        if (null != authorization && authorization.startsWith("Bearer ")) {
            token = authorization.substring("Bearer ".length());
            try {
                Map<String, Object> objectMap = (Map<String, Object>) jwtUtil.getAllClaimsFromToken(token).get("user");
//                String username = objectMap.get("userName").toString();
//                String emailId = objectMap.get("emailId").toString();
                ObjectMapper objectMapper = new ObjectMapper();
                userMaster=objectMapper.convertValue(objectMap, UserMaster.class);
//                ExamManagementMaster examManagementMaster = objectMapper.convertValue(objectMap.get("examManagementId"), ExamManagementMaster.class);
//                userMaster = userRepository.findByUserNameAndEmailIdAndExamManagementId(username, emailId, examManagementMaster);
                System.out.println(userMaster.toString());
            } catch (Exception e) {
            	e.printStackTrace();
                ApiException except = new ApiException(
                        e.getMessage(),
                        HttpStatus.BAD_REQUEST,
                        null,
                        null
                );
            }
        } else {
            log.error("Jwt Token does not start with Bearer");
        }
       return userMaster;
    }
    @Override
    public Map<String,Object> checkFileValid(MultipartFile mpFile) {
		Map<String, Object> resp = new HashMap<String, Object>();
		boolean isNullByteProof = true;
		final List<String> allowedFileExtentions = Arrays.asList(new String[]{"xls", "xlsx"});//add other allowed extensions here
		final List<String> allowedMimeTypes = Arrays.asList(new String[]{"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet","application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword",});//add other allowed mime types here

		if(null == mpFile || mpFile.isEmpty()){
			System.out.println("file is empty.");
			resp.put("status", false);
			resp.put("msg", "File should'nt be blank");
			return resp;
		}
		String fileOriginalName;
		try {
			fileOriginalName = URLDecoder.decode(mpFile.getOriginalFilename(), "UTF-8");
			char[] charArray = fileOriginalName.toCharArray();
			for(int a=0;a<charArray.length;a++){
				if(charArray[a] == 0){
				isNullByteProof = false;
				break;
				}
			}
		if(!isNullByteProof){
			System.out.println("File is not null byte proof.");
			resp.put("status", false);
			resp.put("msg", "File is null.");
			return resp;
		}
			
		if(fileOriginalName.indexOf('.', fileOriginalName.indexOf('.') + 1) != -1){
			  System.out.println("File name has double extenstion.");
			  resp.put("status", false);
			  resp.put("msg", "File name has double extenstion.");
			  return resp;
		}
		if(!allowedFileExtentions.contains(FilenameUtils.getExtension(fileOriginalName).toLowerCase())){
			  System.out.println("Invalid file extenstion detected.");
			  resp.put("status", false);
			  resp.put("msg", "Invalid file extenstion detected.");
			  return resp;
		}
			 
		if(!allowedMimeTypes.contains(mpFile.getContentType())){
			System.out.println("Invalid mimetype detected.");
			System.out.println("file extension is "+mpFile.getContentType());
			resp.put("status", false);
			resp.put("msg", "Invalid mimetype detected.");
			return resp;
		}
		//check deep mime type
		try {
		String mimeType = Magic.getMagicMatch(mpFile.getBytes(), false).getMimeType();
		System.out.println("MimeType detected :"+mimeType);
		
		 if(!allowedMimeTypes.contains(mimeType)){
		 System.out.println("Invalid mimetype1 detected."); 
		 resp.put("status", false);
		 resp.put("msg", "Invalid mimetype detected."); return resp; }
		
		if(mpFile.getSize() >= maxUploadSize){
			System.out.println("File size exceeds");
			resp.put("status", false);
			resp.put("msg", "Please upload the file less then 2MB.");
			return resp;
		}
		} catch (MagicParseException | MagicMatchNotFoundException| MagicException | IOException e) {
			e.printStackTrace();
			resp.put("status", false);
			resp.put("msg", "Magic mime type exceptions occured");
			return resp;
		}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			resp.put("status", false);
			resp.put("msg", "Some exception occured");
			return resp;
		}
		resp.put("status", true);
		resp.put("msg", "File validation successfully completed.");
		return resp;
	}//end of isValidFile

    @Override
	public String saveFileInPhysicalPath(MultipartFile file, String directory, String fileName) throws IOException {
		String filepathlist = "";
		System.out.println("Saved File Path : " + directory);
		if (!file.isEmpty()) {
			Path path = null;
			File dir = new File(directory);
			if (!dir.exists())
				dir.mkdirs();
			byte[] bytes = file.getBytes();
			path = Paths.get(directory + fileName);
			System.out.println("Saved File Path : " + path + "before file write");
			Files.write(path, bytes);
			System.out.println("Saved File Path : " + path + "before file write");
			String fileNameDetails = path.getFileName().toString();
			String pdirectory = path.getParent().toString();
			System.out.println("fileName " + fileNameDetails);
			System.out.println("pdirectory " + pdirectory);
			filepathlist = path.toString();
			System.out.println("Saved File Path : " + path.toString());
			System.out.println("Saved File Path : " + path.toString());
		}
		return filepathlist;
	}

}
