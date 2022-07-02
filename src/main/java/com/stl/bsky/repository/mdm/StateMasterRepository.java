package com.stl.bsky.repository.mdm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stl.bsky.entity.mdm.StateMaster;

@Repository
public interface StateMasterRepository extends JpaRepository<StateMaster, String>{
}
