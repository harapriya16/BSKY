package com.stl.bsky.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
	
//	@ExceptionHandler(EmptyInputException.class)
//	public ResponseEntity<Object> handleEmptyInput(EmptyInputException emptyInput, WebRequest request) {
//		// 1. create payload containing exception details
//		ApiException except=new ApiException(
//				emptyInput.getMessage(), 
//				HttpStatus.BAD_REQUEST,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//			);
//		// 2. Return response entity
//		
//		return new ResponseEntity<>(except, HttpStatus.BAD_REQUEST);
//	}
//	
//	@ExceptionHandler(NoSuchElementException.class)
//	public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException emptyInput, WebRequest request) {
//		ApiException except=new ApiException(
//				emptyInput.getMessage(), 
//				HttpStatus.NOT_FOUND,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//			);
//		return new ResponseEntity<>(except, HttpStatus.NOT_FOUND);
//	}
//	
//	@Override
//	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
//			HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//		ApiException except=new ApiException(
//				ex.getMessage(), 
//				HttpStatus.METHOD_NOT_ALLOWED,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//			);
//		return new ResponseEntity<Object>(except, HttpStatus.METHOD_NOT_ALLOWED);
//	}
//
//	@Override
//	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//		return ResponseEntity.status(400).body(new ApiException(
//				ex.getMessage(),
//				HttpStatus.BAD_REQUEST,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//		));
//	}
//
//	@Override
//	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//		List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
//		if (!CollectionUtils.isEmpty(mediaTypes)) {
//			headers.setAccept(mediaTypes);
//			if (request instanceof ServletWebRequest) {
//				ServletWebRequest servletWebRequest = (ServletWebRequest)request;
//				if (HttpMethod.PATCH.equals(servletWebRequest.getHttpMethod())) {
//					headers.setAcceptPatch(mediaTypes);
//				}
//			}
//		}
//
////		return this.handleExceptionInternal(ex, (Object)null, headers, status, request);
//
//		return new ResponseEntity(new ApiException(
//				ex.getMessage(),
//				HttpStatus.BAD_REQUEST,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//		), headers, HttpStatus.BAD_REQUEST);
//	}
//	@ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ApiException> handleAccessDeniedException(BadCredentialsException e, WebRequest request){
//        return ResponseEntity.status(403).body(new ApiException(
//				e.getMessage(), 
//				HttpStatus.FORBIDDEN,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//			));
//    }
//	
//	@ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<ApiException> handleJwtExpiredException(ExpiredJwtException e, WebRequest request){
//        return ResponseEntity.status(403).body(new ApiException(
//				e.getMessage(), 
//				HttpStatus.BAD_REQUEST,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//			));
//    }
//	
//	@ExceptionHandler(SignatureException.class)
//    public ResponseEntity<ApiException> handleSignatureException(SignatureException e, WebRequest request){
//        return ResponseEntity.status(403).body(new ApiException(
//				e.getMessage(), 
//				HttpStatus.FORBIDDEN,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//			));
//    }
//
//	@ExceptionHandler(InvalidFileException.class)
//	public ResponseEntity<Object> handleInvalidFileException(InvalidFileException emptyInput, WebRequest request) {
//		// 1. create payload containing exception details
//		ApiException except=new ApiException(
//				emptyInput.getMessage(),
//				HttpStatus.BAD_REQUEST,
//				ZonedDateTime.now(ZoneId.of("Z")),
//				request.getDescription(false)
//		);
//		// 2. Return response entity
//
//		return new ResponseEntity<>(except, HttpStatus.NOT_ACCEPTABLE);
//	}
}
