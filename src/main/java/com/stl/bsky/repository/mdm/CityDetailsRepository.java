package com.stl.bsky.repository.mdm;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stl.bsky.entity.mdm.CityDetailsEntity;

import java.util.List;

public interface CityDetailsRepository extends JpaRepository<CityDetailsEntity, Long>{
    List<CityDetailsEntity> findByStateMasterStateCode(String stateCode);
}
