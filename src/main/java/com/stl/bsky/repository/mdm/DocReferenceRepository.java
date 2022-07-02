package com.stl.bsky.repository.mdm;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stl.bsky.entity.mdm.DocReference;

import java.util.Optional;

public interface DocReferenceRepository extends JpaRepository<DocReference, Long> {
    Optional<DocReference> findByIdAndTabReferenceId(Long docRefId, Long tabRefId);
}