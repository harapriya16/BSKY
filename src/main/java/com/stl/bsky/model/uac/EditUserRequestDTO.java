package com.stl.bsky.model.uac;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class EditUserRequestDTO {

    @NotEmpty
    private String userName;
    private String title;
    private String firstName;
    private String lastName;
    @NotNull
    private String emailId;
    @NotNull
    private String mobileNo;
    private int roleId;
    private String updatedBy;
    private String profilePic;
    //    private String profilePicName;
    private int statusId;
}
