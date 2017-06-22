package com.flipkart.smartgrocery.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.flipkart.smartgrocery.R;
import com.flipkart.smartgrocery.adapters.ProductListAdapter;
import com.flipkart.smartgrocery.netowking.HackdayService;
import com.flipkart.smartgrocery.netowking.RetrofitApiClient;
import com.flipkart.smartgrocery.netowking.response.ProductModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_FOR_BARCODE_SCANNING = 1;

    private Button scanButton;
    private ListView productsListView;

    private HackdayService hackdayService = RetrofitApiClient.getClient().create(HackdayService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = (Button) findViewById(R.id.scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, BarCodeScannerActivity.class), REQUEST_CODE_FOR_BARCODE_SCANNING);
            }
        });

        productsListView = (ListView) findViewById(R.id.product_list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, requestCode + " " + resultCode + " " + data);
        if (requestCode == REQUEST_CODE_FOR_BARCODE_SCANNING && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                String searchTerm = extras.getString(BarCodeScannerActivity.INTENT_EXTRA_SCAN_RESULT);
                Log.i(TAG, searchTerm);

                searchProducts(searchTerm);
            }
        }
    }

    private void searchProducts(String searchTerm) {
        Call<List<ProductModel>> searchResults = hackdayService.getSearchResults(searchTerm);
        searchResults.enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                handleResponse(response.body());
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error getting Product, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleResponse(List<ProductModel> products) {
        ProductListAdapter adapter = new ProductListAdapter(products, this);
        productsListView.setAdapter(adapter);
    }
}
