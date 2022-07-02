package com.stl.bsky.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.exception.ApiException;
import com.stl.bsky.service.CustomUserDetailsService;
import com.stl.bsky.utility.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LogManager.getLogger(JwtFilter.class);


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorization = request.getHeader("Authorization");
        String token = null;
        String userName = null;
        UserMaster userMaster=null;
        if (null != authorization) {
            if (authorization.startsWith("Bearer ")) {
                token = authorization.substring("Bearer ".length());
                logger.info("Inside JwtFilter filter given token {}", token);
                try {
                	Map<String, Object> objectMap = (Map<String, Object>) jwtUtil.getAllClaimsFromToken(token).get("user");
                  ObjectMapper objectMapper = new ObjectMapper();
                   userMaster=objectMapper.convertValue(objectMap, UserMaster.class);
                 //userName = jwtUtil.getUsernameFromToken(token);
                   userName=userMaster.getUserName();
                } catch (IllegalArgumentException | ExpiredJwtException e) {
                    logger.error(e.getMessage());
                    ApiException except = new ApiException(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST,
                            null,
                            null
                    );
                    byte[] exceptionObj = new ObjectMapper().writeValueAsBytes(except);
                    response.getOutputStream().write(exceptionObj);
                    return;
                }
            } else {
                logger.error("Jwt Token does not start with Bearer");
            }
        }
        	System.out.println("userName"+userName);
        if (null != userName && null == SecurityContextHolder.getContext().getAuthentication()) {
        	try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
            logger.info("Inside JwtFilter filter {}", userDetails);
            if (jwtUtil.validateToken(token, userDetails)) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("authenticated user " + userName + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }
        filterChain.doFilter(request, response);
    }
}
