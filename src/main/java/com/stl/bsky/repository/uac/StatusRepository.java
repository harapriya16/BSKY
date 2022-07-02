package com.stl.bsky.repository.uac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stl.bsky.entity.uac.StatusMaster;

@Repository
public interface StatusRepository extends JpaRepository<StatusMaster, Integer> {
    StatusMaster findByStatusId(int activeStatus);
}
