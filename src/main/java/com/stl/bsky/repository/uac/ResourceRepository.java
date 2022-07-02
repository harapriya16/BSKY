package com.stl.bsky.repository.uac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stl.bsky.entity.uac.ResourceMaster;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceMaster, Long> {

    @Query("select max(resourceId) from ResourceMaster")
    Long getMaxId();

    ResourceMaster findByResourceId(Long resource_id);

    List<ResourceMaster> findAllByParentResourceAndOrderInEqualsAndStatusStatusId(ResourceMaster parentResource, @NotNull(message = "Order cannot be null")Integer orderIn, Integer statusId);
    List<ResourceMaster> findAllByParentResourceAndOrderInEqualsAndStatusStatusIdAndResourceIdNot(ResourceMaster parentResource, Integer orderIn, Integer statusId, Long resourceId);
    List<ResourceMaster> findAllByParentResourceResourceIdOrderByOrderIn(Long resourceId);
}
