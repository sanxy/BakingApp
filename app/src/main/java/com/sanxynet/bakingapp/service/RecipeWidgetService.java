package com.sanxynet.bakingapp.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.DecimalFormat;

import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.db.BakingAppContract;
import com.sanxynet.bakingapp.utils.PrefManager;

public class RecipeWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeRemoteViewsFactory(this.getApplicationContext());
    }

    class RecipeRemoteViewsFactory implements RemoteViewsFactory {
        final Context mContext;
        Cursor mCursor;


        RecipeRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext.getApplicationContext();
        }


        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            int recipeId = PrefManager.getIntPref(mContext, R.string.pref_widget_id);

            if (mCursor != null) mCursor.close();

            final long identityToken = Binder.clearCallingIdentity();

            mCursor = mContext.getContentResolver().query(BakingAppContract.IngredientEntry.CONTENT_URI,
                    null,
                    BakingAppContract.IngredientEntry.COLUMN_NAME_RECIPES_ID + " = ?",
                    new String[]{String.valueOf(recipeId)},
                    BakingAppContract.IngredientEntry._ID + " ASC");

            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            if (mCursor == null || mCursor.getCount() == 0) return null;

            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.recipe_list_widget);

            mCursor.moveToPosition(position);

            String ingredientName = mCursor.getString(mCursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_NAME_INGREDIENT));
            float quantityName = mCursor.getFloat(mCursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_NAME_QUANTITY));
            String measureName = mCursor.getString(mCursor.getColumnIndex(BakingAppContract.IngredientEntry.COLUMN_NAME_MEASURE));

            views.setTextViewText(R.id.recipe_widget_ingredient_name, ingredientName);

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            views.setTextViewText(R.id.recipe_widget_ingredient_quantity, String.valueOf(decimalFormat.format(quantityName)));

            measureName = "(" + measureName + ")";
            views.setTextViewText(R.id.recipe_widget_ingredient_measure, measureName);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
