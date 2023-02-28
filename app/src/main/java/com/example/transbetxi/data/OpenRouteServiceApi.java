package com.example.transbetxi.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenRouteServiceApi {

    @GET("directions/driving-car")
    Call<DirectionsResponse> getDirections(
            @Query("api_key") String apiKey,
            @Query("start") String start,
            @Query("end") String end
    );
}
