package com.example.solutionsproject.model.gson.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

@Getter
public class ApplicantsGson extends GsonData {
    //private List<ApplicantsGson> applicants;

    @SerializedName("creatorApplicantId")
    private int creatorApplicantId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("status")
    private String status;

    @SerializedName("dateAdded")
    private String dateAdded;

    @SerializedName("dateUpdated")
    private String dateUpdated;
}
