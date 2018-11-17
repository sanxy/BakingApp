package com.sanxynet.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.sanxynet.bakingapp.utils.Costants;

public class Recipe implements Parcelable {


    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_ID)
    private Integer id;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_NAME)
    private String name;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_INGREDIENTS)
    private ArrayList<Ingredient> mIngredientArrayList;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_STEPS)
    private ArrayList<Step> mStepArrayList;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_SERVINGS)
    private Integer servings;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_IMAGE)
    private String image;


    public Integer getId() {
        return id;
    }

    public Integer getServings() {
        return servings;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return mIngredientArrayList;
    }

    public ArrayList<Step> getStepList() {
        return mStepArrayList;
    }

    public String getImage() {
        return image;
    }


    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        mIngredientArrayList = new ArrayList<>();
        in.readTypedList(mIngredientArrayList, Ingredient.CREATOR);
        mStepArrayList = new ArrayList<>();
        in.readTypedList(mStepArrayList, Step.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(mIngredientArrayList);
        dest.writeTypedList(mStepArrayList);
        dest.writeInt(servings);
        dest.writeString(image);
    }
}
