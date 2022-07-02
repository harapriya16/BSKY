package com.stl.bsky.repository.mdm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.stl.bsky.common.GenericSpecification;
import com.stl.bsky.entity.mdm.IconDetails;

import java.util.List;

public interface IconRepository extends JpaRepository<IconDetails, Long>, JpaSpecificationExecutor<IconDetails>  {

    Page<IconDetails> findAllByStatusStatusId(int statusId, Pageable pageable);

    List<IconDetails> findAllByStatusStatusId(int statusId);

    List<IconDetails> findAllByStatusStatusId(int statusId, Sort by);

	List<IconDetails> findAllByImageName(String imageName);

	List<IconDetails> findAllByFaText(String faText);

	Page<IconDetails> findAllByIconType(String value, Pageable pageable);
	

}
