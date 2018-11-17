package com.sanxynet.bakingapp.remote;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import com.sanxynet.bakingapp.db.DataUtils;
import com.sanxynet.bakingapp.idling.SimpleIdlingResource;
import com.sanxynet.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetroCall {

    private final RetroManager retroManager;
    private ArrayList<Recipe> mRecipeArrayList;

    public RetroCall() {
        mRecipeArrayList = new ArrayList<>();
        retroManager = RetroManager.getInstance();
    }

    public void loadData(final RestData myCallBack, final SimpleIdlingResource simpleIdlingResource) {
        if(simpleIdlingResource!=null){
            simpleIdlingResource.setIdleState(false);
        }

        Callback<ArrayList<Recipe>> callback = new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {

                mRecipeArrayList = response.body();

                if (response.isSuccessful()) {
                    myCallBack.onRestData(mRecipeArrayList);
                    if(simpleIdlingResource!=null){
                        simpleIdlingResource.setIdleState(true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                call.cancel();
                if (call.isCanceled()) {
                    myCallBack.onErrorData(t);
                }
            }
        };
        retroManager.getUdacityRecipe(callback);

    }

    public void syncData(final Context context) {
        Callback<ArrayList<Recipe>> callback = new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {

                mRecipeArrayList = response.body();

                if (response.isSuccessful()) {
                    DataUtils dataUtils = new DataUtils(context);
                    dataUtils.saveDB(mRecipeArrayList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                call.cancel();
            }
        };
        retroManager.getUdacityRecipeSync(callback);

    }

    public void cancelRequest() {
        if (retroManager != null) {
            retroManager.cancelRequest();
        }
    }

    public interface RestData {
        void onRestData(ArrayList<Recipe> listenerData);
        void onErrorData(Throwable t);
    }
}
