package com.stl.bsky.entity.uac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stl.bsky.entity.mdm.ExamManagementMaster;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_master", schema = "admin")
public class UserMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "User Name is mandatory")
    @Column(name = "user_name", unique = true)
    private String userName;

    @Column(name = "title")
    private String title;

    @NotBlank(message = "Please provide first name")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Please provide last name")
    @Column(name = "last_name")
    private String lastName;

    @Email
    @NotBlank(message = "Please provide email id")
    @Column(name = "email_id")
    private String emailId;

    @NotBlank(message = "Please provide contact no")
    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "last_login")
    private Date lastLogin;
    
    @ManyToOne
	@JoinColumn(name = "exam_management_id")
	private ExamManagementMaster examManagementId;


    @JsonIgnore
    @Column(name = "verification_code")
    private String verificationCode;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @JsonIgnore
    @Column(name = "change_pwd_flag")
    private Boolean changePwdFlag = false;

    @Column(name = "profile_pic")
    private String profilePic;

    @ManyToOne
    @JoinColumn(name = "status_id")
    @JsonIgnoreProperties("statusDescription")
    private StatusMaster status;

    @JsonIgnore
    @Column(name = "created_by")
    private String createdBy;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_dt", updatable = false)
    private Date createdDt;

    @JsonIgnore
    @Column(name = "updated_by")
    private String updatedBy;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_dt")
    private Date updatedDt;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleMaster role;

    
    private String registrationId;
}
