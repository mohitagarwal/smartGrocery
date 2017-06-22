package com.flipkart.smartgrocery.netowking;

import retrofit2.http.GET;
import retrofit2.http.Query;

public class ApiInterface {

    @GET("directions/json?")
    Call<MapsDirectionResponse> getDuration(@Query("origin") String origin, @Query("destination") String destination, @Query("departure_time") long departureTime, @Query("traffic_model") String trafficModel, @Query("key") String key );

}
