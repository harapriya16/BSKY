package com.stl.bsky.model.uac;

import lombok.Data;

@Data
public class UserInfo {
    private long id;
    private String userName;
    private String title;
    private String firstName;
    private String lastName;
    private String mobileNo;
    private String emailId;
    private int roleId;
    private int statusId;
    private String createdOn;
    private String createdBy;
    private String updatedOn;
    private String updatedBy;
    private String profilePic;
}
