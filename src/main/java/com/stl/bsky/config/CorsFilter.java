package com.stl.bsky.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {
    @Override
    @CrossOrigin
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4001"); //Origin http://localhost:4200
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
//        response.setHeader("Access-Control-Allow-Origin", "http://192.168.0.70:3001");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");//"GET, POST, PUT, DELETE"
        response.setHeader("Access-Control-Max-Age", "1800");
        //response.setHeader("Access-Control-Allow-Headers", "authorization, content-type,captchaKey, xsrf-token, rgid, caseno,Set-Cookie,Origin");
        response.setHeader("Access-Control-Allow-Headers", "proxyId,X-Requested-With,captchaKey,content-type, Authorization,caseno,Set-Cookie,Origin");
        response.addHeader("Access-Control-Expose-Headers", "xsrf-token,captchaKey,Authorization,proxyId,caseno,Set-Cookie,Origin");
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else if ("TRAC".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}