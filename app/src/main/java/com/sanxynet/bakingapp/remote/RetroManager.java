package com.sanxynet.bakingapp.remote;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.sanxynet.bakingapp.model.Recipe;
import com.sanxynet.bakingapp.service.UdacityService;
import com.sanxynet.bakingapp.utils.Costants;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroManager {


    private static UdacityService mUdacityService;
    private static RetroManager mRetroManager;
    private Call<ArrayList<Recipe>> mCall;

    private RetroManager() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Costants.UDACITY_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mUdacityService = retrofit.create(UdacityService.class);

    }

    public static RetroManager getInstance() {
        if (mRetroManager == null) {
            mRetroManager = new RetroManager();
        }
        return mRetroManager;
    }

    public void getUdacityRecipe(Callback<ArrayList<Recipe>> callback) {
        mCall = mUdacityService.getUdacityService();
        mCall.enqueue(callback);
    }

    public void getUdacityRecipeSync(Callback<ArrayList<Recipe>> callback) {
        Call<ArrayList<Recipe>> call = mUdacityService.getUdacityService();
        call.enqueue(callback);
    }

    public void cancelRequest() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

}