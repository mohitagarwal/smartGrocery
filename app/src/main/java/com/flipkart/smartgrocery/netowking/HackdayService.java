package com.flipkart.smartgrocery.netowking;

import com.flipkart.smartgrocery.netowking.response.ProductModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HackdayService {

    @GET("v8/hack/search")
    Call<List<ProductModel>> getSearchResults(@Query("search_term") String searchTerm);
}
