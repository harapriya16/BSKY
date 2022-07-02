package com.stl.bsky.repository.uac;
import com.stl.bsky.entity.mdm.ExamManagementMaster;
import com.stl.bsky.entity.uac.UserMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserMaster, Long>, JpaSpecificationExecutor<UserMaster> {
    UserMaster findByUserName(String userName);

    List<UserMaster> findByEmailIdAndStatusStatusId(String emailId, Integer statusId);

    List<UserMaster> findByEmailIdAndStatusStatusIdAndIdNot(String emailId, Integer statusId, Long id);

    List<UserMaster> findByContactNoAndStatusStatusId(String mobileNo, Integer statusId);

    List<UserMaster> findByContactNoAndStatusStatusIdAndIdNot(String mobileNo, Integer statusId, Long id);

    Optional<UserMaster> findUserMasterByVerificationCode(String verificationCode);

    Optional<UserMaster> findUserMasterByVerificationCodeAndStatusStatusId(String verificationCode, Integer id);

    Optional<UserMaster> findByUserNameAndEmailId(String contactNo, String emailId);
    
    @Transactional
    @Modifying
    @Query(value = "UPDATE admin.user_master SET first_name=?, last_name=?,profile_pic=? WHERE id = ?", nativeQuery = true)
	Integer updateProfileDetails(String firstName, String lastName, String fileName, Long id);
}
