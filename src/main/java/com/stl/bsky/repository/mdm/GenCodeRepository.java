package com.stl.bsky.repository.mdm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stl.bsky.entity.mdm.GenCodeEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenCodeRepository extends JpaRepository<GenCodeEntity, Long> {
    Optional<GenCodeEntity> findByName(String genCodeName);

    List<GenCodeEntity> findByParentIdAndIsActive(int parentId,int isActive);

}
