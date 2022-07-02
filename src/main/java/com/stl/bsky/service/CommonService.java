package com.stl.bsky.service;

import com.stl.bsky.common.ImageUpload;
import com.stl.bsky.common.PagingAndSearching;
import com.stl.bsky.entity.uac.UserMaster;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public interface CommonService {
    Pageable setPageable(PagingAndSearching pagination);

    Pageable getPageAndSort(PagingAndSearching pagination);

    //void checkAndSaveAttachment(ImageUpload upload, Long refId, Long tabRefId, Long docRefId);

    //ImageUpload getFileFromPhysicalPath(Long refId, Long tabRefId, Long docRefId) throws IOException;

    UserMaster extractUserFromToken(HttpServletRequest request);
    Map<String,Object> checkFileValid(MultipartFile mpFile);
    String saveFileInPhysicalPath(MultipartFile file, String directory, String fileName) throws IOException ;

	String checkAndSaveAttachmentInUserMaster(String image);

	//void checkAndSaveAttachmentInUserMaster(ImageUpload upload, Long refId);
}
