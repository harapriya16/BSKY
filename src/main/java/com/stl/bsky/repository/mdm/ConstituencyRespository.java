package com.stl.bsky.repository.mdm;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stl.bsky.entity.mdm.Constituency;

@Repository
public interface ConstituencyRespository extends JpaRepository<Constituency, Integer>{

	Optional<List<Constituency>> findByDistrictMasterDistrictCode(Integer districtCode);

}
