package com.stl.bsky.repository.mdm;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stl.bsky.entity.mdm.ResourceType;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, Integer> {
	
	ResourceType findByResourceName(String resourceName);
	
}