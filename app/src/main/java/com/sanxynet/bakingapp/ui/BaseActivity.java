package com.sanxynet.bakingapp.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.db.BakingAppContract;
import com.sanxynet.bakingapp.utils.Costants;
import com.sanxynet.bakingapp.utils.PrefManager;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressWarnings({"WeakerAccess", "CanBeFinal", "unused"})
    @BindView(R.id.content_frame)
    FrameLayout frameLayout;

    private int mLayoutResource;
    private static WeakReference<Context> mWeakReference;
    private boolean mEnableNavigationView;

    private static String mRecipeName;
    private static int mRecipeId;
    private static int mPositionStep = RecyclerView.NO_POSITION;
    private static int mPositionIngredient = RecyclerView.NO_POSITION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeakReference = new WeakReference<>(getApplicationContext());
        ViewStub mStub;
        if (isEnableNavigationView()) {
            setContentView(R.layout.activity_base);

            if (getLayoutResource() > 0) {

                mStub = findViewById(R.id.stub_base_layout);
                mStub.setLayoutResource(getLayoutResource());
                mStub.inflate();

            }
            ButterKnife.bind(this);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView mNavigationView = findViewById(R.id.nav_view);

            switch (getLayoutResource()) {
                case R.layout.activity_detail:
                    mNavigationView.inflateMenu(R.menu.activity_base_drawer_detail);
                    break;
                case R.layout.activity_step:
                    mNavigationView.inflateMenu(R.menu.activity_base_drawer_step);
                    break;
                case R.layout.activity_main:
                default:
                    mNavigationView.inflateMenu(R.menu.activity_base_drawer_main);
            }

            mNavigationView.setNavigationItemSelectedListener(this);

        } else {
            setContentView(R.layout.activity_base_no_navigation);

            if (getLayoutResource() > 0) {

                mStub = findViewById(R.id.stub_base_layout);
                mStub.setLayoutResource(getLayoutResource());
                mStub.inflate();

            }
            ButterKnife.bind(this);
        }

    }

    @Override
    public void onBackPressed() {
        if (isEnableNavigationView()) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if ((drawer != null) && (drawer.isDrawerOpen(GravityCompat.START))) {
                drawer.closeDrawer(GravityCompat.START);

            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                closeNotification();
                startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                item.setEnabled(true);
                break;
            case R.id.nav_ingredients:
                if (getRecipeId() > 0) {
                    closeNotification();
                    item.setEnabled(true);
                } else {
                    item.setEnabled(false);
                }
                openNavDetailActivity(Costants.TAB_ORDER_INGREDIENT);

                break;
            case R.id.nav_steps:
                if (getRecipeId() > 0) {
                    closeNotification();
                    item.setEnabled(true);
                } else {
                    item.setEnabled(false);
                }
                openNavDetailActivity(Costants.TAB_ORDER_STEP);

                break;
            case R.id.nav_settings:
                closeNotification();
                startActivity(new Intent(this, SettingsActivity.class));
                item.setEnabled(true);

                break;
            case R.id.nav_share:
                if (getRecipeId() > 0) {
                    new ShoppingListAsyncTask().execute(getRecipeId());
                    item.setEnabled(true);
                } else {
                    item.setEnabled(false);
                }

                break;
            case R.id.nav_send:

                if (sendNavUrl()) {
                    item.setEnabled(true);
                    sendNavUrl();
                } else {
                    item.setEnabled(false);
                }

                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    FrameLayout getFrameLayout() {
        return frameLayout;
    }

    int getLayoutResource() {
        return mLayoutResource;
    }

    void setLayoutResource(int layoutResource) {
        mLayoutResource = layoutResource;
    }

    private boolean isEnableNavigationView() {
        return mEnableNavigationView;
    }

    void setEnableNavigationView(boolean enableNavigationView) {
        mEnableNavigationView = enableNavigationView;
    }

    private void openNavDetailActivity(int orderTab) {
        if (getRecipeId() > 0) {
            startActivity(new Intent(this, DetailActivity.class)
                    .putExtra(Costants.EXTRA_RECIPE_ID, getRecipeId())
                    .putExtra(Costants.EXTRA_RECIPE_NAME, getRecipeName())
                    .putExtra(Costants.EXTRA_TAB_ORDERTAB, orderTab)
            );
        }
    }

    boolean sendNavUrl() {
        String videoUrl = PrefManager.getStringPref(getApplicationContext(), R.string.pref_video_uri);

        if (!TextUtils.isEmpty(videoUrl)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
            return true;
        }
        return false;
    }

    static class ShoppingListAsyncTask extends AsyncTask<Integer, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Integer... integers) {

            Context context = mWeakReference.get();

            final Uri uri = BakingAppContract.IngredientEntry.CONTENT_URI;

            final String selection = BakingAppContract.IngredientEntry.COLUMN_NAME_RECIPES_ID + "  = ?";

            final String[] argSelection = new String[]{String.valueOf(integers[0])};

            return context.getContentResolver().query(uri, null, selection, argSelection,
                    null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            Context context = mWeakReference.get();
            StringBuilder builder = new StringBuilder();
            try {
                if ((cursor != null) && (!cursor.isClosed())) {
                    while (cursor.moveToNext()) {
                        if ((cursor.getPosition() == 0) && (getRecipeName() != null)) {
                            builder.append("Recipe: ").append(getRecipeName()).append("\n\n");
                        }
                        builder.append(cursor.getPosition() + 1).append(".").append("\n");
                        builder.append("Ingredient: ");
                        builder.append(cursor.getString(cursor.getColumnIndex(
                                BakingAppContract.IngredientEntry.COLUMN_NAME_INGREDIENT))).append("\n");
                        builder.append("Quantity: ");
                        builder.append(cursor.getString(cursor.getColumnIndex(
                                BakingAppContract.IngredientEntry.COLUMN_NAME_QUANTITY))).append("\t");
                        builder.append(cursor.getString(cursor.getColumnIndex(
                                BakingAppContract.IngredientEntry.COLUMN_NAME_MEASURE))).append("\n");
                        if (cursor.getPosition() != (cursor.getCount() - 1)) {
                            builder.append("------------------------------------------------------------").append("\n");
                        } else {
                            builder.append("\n\n\n");
                        }

                        if (cursor.isLast()) {
                            builder.append("------------------------------------------------------------").append("\n");
                            builder.append(context.getString(R.string.app_name)).append("\n");
                            builder.append("------------------------------------------------------------");
                        }

                    }
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                            "Recipe: " + getRecipeName());

                    shareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(shareIntent);
                }
            } finally {
                if ((cursor != null) && (!cursor.isClosed())) {
                    cursor.close();
                }
            }
        }
    }

    private void closeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(Costants.NOTIFICATION_CHANNEL_ID, Costants.NOTIFICATION_ID);
        }
    }

    public static String getRecipeName() {
        return mRecipeName;
    }

    static int getRecipeId() {
        return mRecipeId;
    }

    public static int getPositionStep() {
        return mPositionStep;
    }

    public static int getPositionIngredient() {
        return mPositionIngredient;
    }

    static void setRecipeId(int recipeId) {
        mRecipeId = recipeId;
    }

    static void setRecipeName(String recipeName) {
        mRecipeName = recipeName;
    }

    static void setPositionIngredient(int positionIngredient) {
        mPositionIngredient = positionIngredient;
    }

    public static void setPositionStep(int positionStep) {
        mPositionStep = positionStep;
    }

    public static void clearRecipeId() {
        mRecipeId = 0;
    }

    static void clearPosition() {
        mPositionIngredient = RecyclerView.NO_POSITION;
        mPositionStep = RecyclerView.NO_POSITION;
    }

}
