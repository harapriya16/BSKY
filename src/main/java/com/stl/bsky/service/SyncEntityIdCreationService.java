package com.stl.bsky.service;

import com.stl.bsky.entity.uac.ResourceMaster;
import com.stl.bsky.entity.uac.RoleMaster;

public interface SyncEntityIdCreationService {
    void saveRole(RoleMaster role);

    void saveResource(ResourceMaster resource);
}
