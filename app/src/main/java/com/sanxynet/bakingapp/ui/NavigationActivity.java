package com.sanxynet.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.db.BakingAppContract;
import com.sanxynet.bakingapp.utils.Costants;

public class NavigationActivity extends AppCompatActivity {

    private static WeakReference<Context> mWeakReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeakReference = new WeakReference<>(getApplicationContext());
        Intent intent = getIntent();
        if (intent != null) {
            Navigation navigation = new Navigation();
            navigation.setIntendId(intent.getIntExtra(Costants.EXTRA_DETAIL_STEP_ID, -1));
            navigation.setRecipeName(intent.getStringExtra(Costants.EXTRA_RECIPE_NAME));
            navigation.setRecipeId(intent.getIntExtra(Costants.EXTRA_RECIPE_ID, -1));

            int navigationType = intent.getIntExtra(Costants.EXTRA_NAVIGATION_TYPE, 0);

            switch (navigationType) {

                case R.id.navigation_back:
                    new NavigationBackAsyncTask().execute(navigation);
                    break;

                case R.id.navigation_forward:
                    new NavigationForwardAsyncTask().execute(navigation);
                    break;

                case R.string.device_type_tablet:
                    tabletResult(navigation.getIntendId(), navigation.getRecipeName());
            }

        }
    }

    private void tabletResult(int id, String recipeName) {
        Intent send = new Intent(NavigationActivity.this, StepActivity.class);
        send.putExtra(Costants.EXTRA_DETAIL_STEP_ID, id);
        send.putExtra(Costants.EXTRA_RECIPE_NAME, recipeName);
        send.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(send);
    }

    private static class NavigationBackAsyncTask extends AsyncTask<Navigation, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Navigation... args) {
            Context context = mWeakReference.get();
            final Uri uri = BakingAppContract.StepEntry.CONTENT_URI;

            final String[] arrProjection = new String[]{
                    BakingAppContract.StepEntry._ID};

            final String selection = BakingAppContract.StepEntry.COLUMN_NAME_RECIPES_ID + "  = ?";

            final String[] argSelection = new String[]{String.valueOf(args[0].getRecipeId())};

            Cursor cursor = context.getContentResolver().query(uri, arrProjection, selection, argSelection,
                    BakingAppContract.StepEntry._ID + " DESC ");

            if ((cursor != null) && (!cursor.isClosed())) {
                int[] result = {-1};
                while (cursor.moveToNext()) {
                    result[0] = cursor.getInt(cursor.getColumnIndex(BakingAppContract.StepEntry._ID));
                    if (result[0] < args[0].getIntendId()) {
                        BaseActivity.setPositionStep(BaseActivity.getPositionStep() - 1);
                        Intent send = new Intent(context, StepActivity.class);
                        send.putExtra(Costants.EXTRA_DETAIL_STEP_ID, result[0]);
                        send.putExtra(Costants.EXTRA_RECIPE_NAME, args[0].getRecipeName());
                        send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(send);
                        return cursor;
                    }
                }
            }
            return cursor;
        }


        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if ((cursor != null) && (!cursor.isClosed())) {
                cursor.close();
            }
        }
    }

    private static class NavigationForwardAsyncTask extends AsyncTask<Navigation, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Navigation... args) {

            Context context = mWeakReference.get();
            final Uri uri = BakingAppContract.StepEntry.CONTENT_URI;

            final String[] arrProjection = new String[]{
                    BakingAppContract.StepEntry._ID};

            final String selection = BakingAppContract.StepEntry.COLUMN_NAME_RECIPES_ID + "  = ?";

            final String[] argSelection = new String[]{String.valueOf(args[0].getRecipeId())};

            Cursor cursor = context.getContentResolver().query(uri, arrProjection, selection, argSelection,
                    BakingAppContract.StepEntry._ID + " ASC ");

            if ((cursor != null) && (!cursor.isClosed())) {
                int[] result = {-1};
                while (cursor.moveToNext()) {
                    result[0] = cursor.getInt(cursor.getColumnIndex(BakingAppContract.StepEntry._ID));
                    if (result[0] > args[0].getIntendId()) {
                        BaseActivity.setPositionStep(BaseActivity.getPositionStep() + 1);
                        Intent send = new Intent(context, StepActivity.class);
                        send.putExtra(Costants.EXTRA_DETAIL_STEP_ID, result[0]);
                        send.putExtra(Costants.EXTRA_RECIPE_NAME, args[0].getRecipeName());
                        send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(send);

                        cursor.close();
                        return cursor;
                    }
                }
            }

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if ((cursor != null) && (!cursor.isClosed())) {
                cursor.close();
            }
        }

    }

    private class Navigation {

        private int intendId;
        private int recipeId;
        private String recipeName;

        int getIntendId() {
            return intendId;
        }

        void setIntendId(int intendId) {
            this.intendId = intendId;
        }

        int getRecipeId() {
            return recipeId;
        }

        void setRecipeId(int recipeId) {
            this.recipeId = recipeId;
        }

        String getRecipeName() {
            return recipeName;
        }

        void setRecipeName(String recipeName) {
            this.recipeName = recipeName;
        }

    }
}
