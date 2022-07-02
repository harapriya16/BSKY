package com.stl.bsky.model.uac;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CreateUserRequestDTO {
    @Pattern(regexp ="[a-zA-Z0-9 /]+",message="Enter Valid User Name")
    @NotNull(message = "User name cannot be empty")
    private String userName;

    //    @NotNull(message = "Title cannot be empty")
//    @Pattern(regexp ="[a-zA-Z/]+",message="Enter Valid Tittle")
    private String title;

    @NotNull(message = "First Name cannot be empty")
    @Pattern(regexp ="[a-zA-Z. /]+",message="Enter Valid First Name")
    private String firstName;

    @NotNull(message = "Last name cannot be empty")
    @Pattern(regexp ="[a-zA-Z. /]+",message="Enter Valid Last Name")
    private String lastName;

    @NotNull(message = "Password cannot be empty")
    private String password;

//    @NotNull(message = "User Type cannot be empty")
//    private Long userType;
//
//    private String userTypeName;

    @Email(message = "Enter Valid Email Id")
    @NotNull(message = "Email Id cannot be empty")
    private String emailId;

    //    @Min(value = 10,message="must be less than or equal to 10")
//    @Max(value = 10,message="must be less than or equal to 10")
    private String mobileNo;
    private String createdBy;
    @NotNull(message = "Role cannot be empty")
    int roleId;
    private String profilePic;
//    private String profilePicName;

    private int statusId;

}
