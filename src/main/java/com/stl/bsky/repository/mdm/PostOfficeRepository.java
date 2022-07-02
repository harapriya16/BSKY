package com.stl.bsky.repository.mdm;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stl.bsky.entity.mdm.PostOfficeEntity;

import java.util.Optional;

public interface PostOfficeRepository extends JpaRepository<PostOfficeEntity, Long>{
	Optional<PostOfficeEntity> findByPinCode(Long pinCode);
}
