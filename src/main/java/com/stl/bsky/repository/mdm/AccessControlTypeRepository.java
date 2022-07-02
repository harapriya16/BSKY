package com.stl.bsky.repository.mdm;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stl.bsky.entity.mdm.AccessControlTypeEntity;

@Repository
public interface AccessControlTypeRepository extends JpaRepository<AccessControlTypeEntity, Long>{

	
}
