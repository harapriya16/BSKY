package com.stl.bsky.repository.uac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stl.bsky.entity.uac.RoleMaster;

@Repository
public interface RoleRepository extends JpaRepository<RoleMaster, Long>, JpaSpecificationExecutor<RoleMaster> {
    @Query("select max(roleId) from RoleMaster")
    Integer getMaxId();

    RoleMaster findByRoleId(int id);
}
