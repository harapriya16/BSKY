package com.stl.bsky.service;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface ManagementService {
    ResponseEntity<Object> getAllExamCenterByManagementAgency(HttpServletRequest request);

    ResponseEntity<Object> getAllExamAgenciesByManagementAgency(HttpServletRequest request);

    ResponseEntity<Object> getAllResourcesByManagementAgency(HttpServletRequest request);
}
