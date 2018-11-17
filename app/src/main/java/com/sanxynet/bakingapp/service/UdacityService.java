package com.sanxynet.bakingapp.service;

import java.util.ArrayList;

import com.sanxynet.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UdacityService {
    @GET(" ")
    Call<ArrayList<Recipe>> getUdacityService();
}
