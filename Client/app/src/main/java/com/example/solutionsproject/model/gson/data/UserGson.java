package com.example.solutionsproject.model.gson.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class UserGson extends GsonData{
    @SerializedName("userId")
    @SerializedName("userId")
    private String userId;

    @SerializedName("profilePic")
    private String profilePicUrl;

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("username")
    private String userName;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("role")
    private String role;

    @SerializedName("createdat")
    private String createdAt;

    @SerializedName("updatedat")
    private String updatedAt;
}
