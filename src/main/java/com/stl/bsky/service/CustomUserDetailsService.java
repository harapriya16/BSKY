package com.stl.bsky.service;

import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.repository.uac.UserRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(UserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    	logger.info("inside load user by username");
        System.out.println("=====inside load user by username"+userName);
//        String[] usernameAndmgmtId = userName.split("/");
//        System.out.println("=====inside load user by username"+usernameAndmgmtId[0]+""+Long.valueOf(usernameAndmgmtId[1]));
        UserMaster user = userRepository.findByUserName(userName);
        
        if (null == user) {
            logger.error("{} not found in the database", userName);
            throw new UsernameNotFoundException("User not found in the R");
        } else {
            logger.info("{} found in the database", userName);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleCode()));
        return new User(user.getUserName(), user.getPassword(), authorities);
    }

}
