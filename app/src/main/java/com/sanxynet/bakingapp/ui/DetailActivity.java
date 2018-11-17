package com.sanxynet.bakingapp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.fragment.IngredientFragment;
import com.sanxynet.bakingapp.fragment.StepFragment;
import com.sanxynet.bakingapp.utils.Costants;
import com.sanxynet.bakingapp.utils.PrefManager;
import com.sanxynet.bakingapp.utils.Utility;
import timber.log.Timber;


@SuppressWarnings("ALL")
public class DetailActivity extends BaseActivity
        implements StepFragment.FragmentInteractionListener, IngredientFragment.FragmentIngredientListener {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.content_detail_tab_fragment)
    FrameLayout mFrameTabLayout;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.content_detail_ingredient_fragment)
    FrameLayout mFrameIngredientLayout;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.content_detail_step_fragment)
    FrameLayout mFrameStepLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayoutResource(R.layout.activity_detail);
        setEnableNavigationView(true);

        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(getLayoutResource(), getFrameLayout(), false);

        Timber.plant(new Timber.DebugTree());

        ButterKnife.bind(this);

        Intent intent = getIntent();


        if (savedInstanceState != null) {
            setRecipeId(savedInstanceState.getInt(Costants.BUNDLE_RECIPE_ID, 0));
            setRecipeName(savedInstanceState.getString(Costants.BUNDLE_RECIPE_NAME));

        } else {
            if (intent != null) {
                setRecipeId(intent.getIntExtra(Costants.EXTRA_RECIPE_ID, 0));
                setRecipeName(intent.getStringExtra(Costants.EXTRA_RECIPE_NAME));
            }

            int orderTab = (intent != null) ? intent.getIntExtra(Costants.EXTRA_TAB_ORDERTAB, Costants.TAB_ORDER_INGREDIENT) : -1;
            startFragment(getRecipeId(), orderTab);
        }
        setTitle(getRecipeName());
        visibleFrameLayout();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemListShopping;
        menuItemListShopping = menu.getItem(0);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            menuItemListShopping.setIcon(
                    new IconicsDrawable(getApplicationContext(), MaterialDesignIconic.Icon.gmi_share)
                            .colorRes(R.color.white)
                            .sizeDp(24)
                            .respectFontBounds(true));
            menuItemListShopping.setVisible(true);

            menuItemListShopping.setVisible(true);
        } else {
            menuItemListShopping.setIcon(
                    new IconicsDrawable(getApplicationContext(), MaterialDesignIconic.Icon.gmi_share)
                            .colorRes(R.color.white)
                            .sizeDp(24)
                            .respectFontBounds(true));
            menuItemListShopping.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_list_shopping:
                new ShoppingListAsyncTask().execute(getRecipeId());
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.homeActivity(this);
    }

    @Override
    public void onFragmentInteraction(int id, int position) {
        setPositionStep(position);
        Intent intent = new Intent(DetailActivity.this, StepActivity.class);
        intent.putExtra(Costants.EXTRA_DETAIL_STEP_ID, id);
        intent.putExtra(Costants.EXTRA_RECIPE_NAME, BaseActivity.getRecipeName());
        startActivity(intent);
    }


    @Override
    public void onFragmentIngredient(int position) {
        setPositionIngredient(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Costants.BUNDLE_RECIPE_ID, getRecipeId());
        outState.putString(Costants.BUNDLE_RECIPE_NAME, getRecipeName());
        super.onSaveInstanceState(outState);
    }

    private void startFragment(int index, int orderTab) {
        if (index >= 0) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();

                IngredientFragment ingredientFragment = new IngredientFragment();
                bundle.putInt(Costants.EXTRA_INGREDIENT_ID, index);
                ingredientFragment.setArguments(bundle);
                transaction.replace(R.id.content_detail_ingredient_fragment, ingredientFragment);

                StepFragment stepFragment = new StepFragment();

                stepFragment.setArguments(bundle);
                bundle.putInt(Costants.EXTRA_STEP_ID, index);
                transaction.replace(R.id.content_detail_step_fragment, stepFragment);

            transaction.commit();
        }
    }

    private void visibleFrameLayout() {
        if (PrefManager.isGeneralSettings(this, getString(R.string.pref_tab_layout))) {
            mFrameTabLayout.setVisibility(View.VISIBLE);
        } else {
            if (!Utility.isTablet(getApplicationContext()) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mFrameIngredientLayout.setVisibility(View.VISIBLE);
            }
            if (Utility.isTablet(getApplicationContext()) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mFrameIngredientLayout.setVisibility(View.VISIBLE);
            }

            mFrameStepLayout.setVisibility(View.VISIBLE);
        }
    }


}