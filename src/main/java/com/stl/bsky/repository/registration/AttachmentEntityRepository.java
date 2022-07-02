package com.stl.bsky.repository.registration;

import com.stl.bsky.entity.registration.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachmentEntityRepository extends JpaRepository<AttachmentEntity, Long> {

    Optional<AttachmentEntity> findByRefIdAndTabTypeIdAndDocTypeId(Long refId, Long tabType, Long docType);

}