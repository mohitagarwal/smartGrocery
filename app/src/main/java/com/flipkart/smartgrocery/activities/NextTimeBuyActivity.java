package com.flipkart.smartgrocery.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.flipkart.smartgrocery.R;
import com.flipkart.smartgrocery.adapters.ProductListAdapter;
import com.flipkart.smartgrocery.netowking.HackdayService;
import com.flipkart.smartgrocery.netowking.RetrofitApiClient;
import com.flipkart.smartgrocery.netowking.response.ReceiptScanResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NextTimeBuyActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private ListView productsListView;


    private HackdayService hackdayService = RetrofitApiClient.getClient().create(HackdayService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_time_buy);

        productsListView = (ListView) findViewById(R.id.product_list);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_file_name), MODE_PRIVATE);
        String detectedText = sharedPreferences.getString(getString(R.string.shared_pref_last_detected_text), null);
        if (detectedText == null) {
            Toast.makeText(this, "Unable to load data from DB", Toast.LENGTH_LONG).show();
        } else {
            sendReceiptOCRData(detectedText);
        }
    }

    private void sendReceiptOCRData(String detectedText) {
        Call<ReceiptScanResponse> receiptScanResponseCall = hackdayService.getReceiptScanResults(detectedText);
        receiptScanResponseCall.enqueue(new Callback<ReceiptScanResponse>() {
            @Override
            public void onResponse(Call<ReceiptScanResponse> call, Response<ReceiptScanResponse> response) {
                handleReceiptScanResponse(response.body());
            }

            @Override
            public void onFailure(Call<ReceiptScanResponse> call, Throwable t) {
                Toast.makeText(NextTimeBuyActivity.this, "Error getting products, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleReceiptScanResponse(ReceiptScanResponse response) {
        ProductListAdapter adapter = new ProductListAdapter(response.getProducts(), this);
        productsListView.setAdapter(adapter);
    }
}
