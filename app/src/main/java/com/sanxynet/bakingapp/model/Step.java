package com.sanxynet.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import com.sanxynet.bakingapp.utils.Costants;

public class Step implements Parcelable {


    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_STEPS_ID)
    private Integer id;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_STEPS_SHORTDESCRIPTION)
    private String shortDescription;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_STEPS_DESCRIPTION)
    private String description;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_STEPS_VIDEOURL)
    private String videoURL;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_STEPS_THUMBNAILURL)
    private String thumbnailURL;

    public Integer getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }


    private Step(Parcel in) {
        id = in.readInt();
        shortDescription = in.readString();
        description = in.readString();
        videoURL = in.readString();
        thumbnailURL = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoURL);
        dest.writeString(thumbnailURL);
    }
}
