package com.flipkart.smartgrocery.netowking;

import com.flipkart.smartgrocery.netowking.response.BarcodeSearchResponse;
import com.flipkart.smartgrocery.netowking.response.ProductModel;
import com.flipkart.smartgrocery.netowking.response.ReceiptScanResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HackdayService {

    @GET("v8/hack/search")
//    @GET("grocery/v1/dummy6/search")
    Call<BarcodeSearchResponse> getSearchResults(@Query("search_term") String searchTerm);

    @GET("v8/hack/search/receipt")
    Call<ReceiptScanResponse> getReceiptScanResults(@Query("search_term") String payload);

}
