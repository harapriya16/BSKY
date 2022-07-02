package com.stl.bsky.service.impl;

import com.stl.bsky.entity.uac.ResourceMaster;
import com.stl.bsky.entity.uac.RoleMaster;
import com.stl.bsky.repository.uac.ResourceRepository;
import com.stl.bsky.repository.uac.RoleRepository;
import com.stl.bsky.service.SyncEntityIdCreationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncEntityIdCreationServiceImpl implements SyncEntityIdCreationService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Override
    public void saveRole(RoleMaster role) {
        Integer id;
        id = roleRepository.getMaxId();
        role.setRoleId(id == null ? 1 : id + 1);
        roleRepository.save(role);
    }

    @Override
    public void saveResource(ResourceMaster resource) {
        Long id = null;
        id = resourceRepository.getMaxId();
        resource.setResourceId(id == null ? 1 : id + 1);
        resourceRepository.save(resource);
    }
}
