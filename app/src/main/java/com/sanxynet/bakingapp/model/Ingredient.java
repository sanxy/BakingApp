package com.sanxynet.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import com.sanxynet.bakingapp.utils.Costants;

public class Ingredient implements Parcelable {

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_INGREDIENTS_QUANTITY)
    private Float quantity;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_INGREDIENTS_MEASURE)
    private String measure;

    @SuppressWarnings("CanBeFinal")
    @SerializedName(Costants.JSON_RECIPE_INGREDIENTS_INGREDIENT)
    private String ingredient;

    public Float getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    @SuppressWarnings("WeakerAccess")
    protected Ingredient(Parcel in) {
        quantity = in.readFloat();
        measure = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }

}
