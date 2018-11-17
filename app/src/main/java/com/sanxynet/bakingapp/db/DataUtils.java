package com.sanxynet.bakingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import java.util.ArrayList;

import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.model.Ingredient;
import com.sanxynet.bakingapp.model.Recipe;
import com.sanxynet.bakingapp.model.Step;

public class DataUtils {

    private final Context mContext;

    public DataUtils(Context context) {
        mContext = context;
    }

    private boolean isRecordData() {

        Cursor cursor = mContext.getContentResolver().query(BakingAppContract.RecipeEntry.CONTENT_URI, null, null, null, null);
        boolean isRecord = ((cursor != null) && (cursor.getCount() > 0));

        if (cursor != null) cursor.close();
        return isRecord;
    }

    private void clearData() {

        mContext.getContentResolver().delete(BakingAppContract.RecipeEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(BakingAppContract.IngredientEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(BakingAppContract.StepEntry.CONTENT_URI, null, null);

    }

    private boolean insertData(ArrayList<Recipe> recipeArrayList) {

        int idRecipe;
        int ingredientResult;
        int stepResult;
        int recipeResult;

        ContentValues[] arrContentValues = new ContentValues[recipeArrayList.size()];

        for (int i = 0; i < recipeArrayList.size(); i++) {

            idRecipe = recipeArrayList.get(i).getId();

            ArrayList<Ingredient> ingredientArrayList = recipeArrayList.get(i).getIngredientList();
            int ingredientListCount = ingredientArrayList.size();

            ArrayList<Step> stepArrayList = recipeArrayList.get(i).getStepList();
            int stepListCount = stepArrayList.size();

            arrContentValues[i] = new ContentValues();

            arrContentValues[i].put(BakingAppContract.RecipeEntry._ID, recipeArrayList.get(i).getId());
            arrContentValues[i].put(BakingAppContract.RecipeEntry.COLUMN_NAME_SERVINGS, recipeArrayList.get(i).getServings());
            arrContentValues[i].put(BakingAppContract.RecipeEntry.COLUMN_NAME_NAME, recipeArrayList.get(i).getName());
            arrContentValues[i].put(BakingAppContract.RecipeEntry.COLUMN_NAME_IMAGE, recipeArrayList.get(i).getImage());

            ContentValues[] arrContentValuesIngredients = new ContentValues[ingredientListCount];

            for (int j = 0; j < ingredientListCount; j++) {
                arrContentValuesIngredients[j] = new ContentValues();

                arrContentValuesIngredients[j].put(BakingAppContract.IngredientEntry.COLUMN_NAME_RECIPES_ID, idRecipe);
                arrContentValuesIngredients[j].put(BakingAppContract.IngredientEntry.COLUMN_NAME_QUANTITY, ingredientArrayList.get(j).getQuantity());
                arrContentValuesIngredients[j].put(BakingAppContract.IngredientEntry.COLUMN_NAME_MEASURE, ingredientArrayList.get(j).getMeasure());
                arrContentValuesIngredients[j].put(BakingAppContract.IngredientEntry.COLUMN_NAME_INGREDIENT, ingredientArrayList.get(j).getIngredient());
            }

            ContentValues[] arrContentValuesSteps = new ContentValues[stepListCount];

            for (int j = 0; j < stepListCount; j++) {
                arrContentValuesSteps[j] = new ContentValues();

                arrContentValuesSteps[j].put(BakingAppContract.StepEntry.COLUMN_NAME_RECIPES_ID, idRecipe);
                arrContentValuesSteps[j].put(BakingAppContract.StepEntry.COLUMN_NAME_ID, stepArrayList.get(j).getId());
                arrContentValuesSteps[j].put(BakingAppContract.StepEntry.COLUMN_NAME_SHORT_DESCRIPTION, stepArrayList.get(j).getShortDescription());
                arrContentValuesSteps[j].put(BakingAppContract.StepEntry.COLUMN_NAME_DESCRIPTION, stepArrayList.get(j).getDescription());
                arrContentValuesSteps[j].put(BakingAppContract.StepEntry.COLUMN_NAME_VIDEO_URL, stepArrayList.get(j).getVideoURL());
                arrContentValuesSteps[j].put(BakingAppContract.StepEntry.COLUMN_NAME_THUMBNAIL_URL, stepArrayList.get(j).getThumbnailURL());
            }

            ingredientResult = mContext.getContentResolver().bulkInsert(BakingAppContract.IngredientEntry.CONTENT_URI, arrContentValuesIngredients);
            stepResult = mContext.getContentResolver().bulkInsert(BakingAppContract.StepEntry.CONTENT_URI, arrContentValuesSteps);
            if (ingredientResult == 0 || stepResult == 0) return false;
        }
        recipeResult = mContext.getContentResolver().bulkInsert(BakingAppContract.RecipeEntry.CONTENT_URI, arrContentValues);
        return recipeResult != 0;
    }


    private void prefInsertDb() {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getString(R.string.pref_insert_data), 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(mContext.getString(R.string.pref_insert_data), true);
        editor.apply();

    }

    @SuppressWarnings("WeakerAccess")
    protected void clearPreferenceDb() {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getString(R.string.pref_insert_data), 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(mContext.getString(R.string.pref_insert_data), false);
        editor.apply();
    }

    public boolean saveDB(ArrayList<Recipe> recipes) {
        if (isRecordData()) clearData();
        if (insertData(recipes)) {
            prefInsertDb();
            return true;
        }
        return false;
    }

    public void ClearDataPrivacy() {
        clearData();
        clearPreferenceDb();
    }
}
