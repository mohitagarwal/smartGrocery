package com.flipkart.smartgrocery.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.flipkart.smartgrocery.R;
import com.flipkart.smartgrocery.adapters.ProductListAdapter;
import com.flipkart.smartgrocery.netowking.HackdayService;
import com.flipkart.smartgrocery.netowking.RetrofitApiClient;
import com.flipkart.smartgrocery.netowking.response.BarcodeSearchResponse;
import com.flipkart.smartgrocery.netowking.response.ProductModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_FOR_BARCODE_SCANNING = 1;
    private static final int REQUEST_CODE_FOR_RECEIPT_SCANNING = 2;

    public static final String INTENT_BARCODE = "barcode";

    private Button scanButton;
    private Button scanReceiptButton;
    private ListView productsListView;
    private View listHeaderText;


    private HackdayService hackdayService = RetrofitApiClient.getClient().create(HackdayService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = (Button) findViewById(R.id.scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BarCodeScannerActivity.class);
                intent.putExtra(BarCodeScannerActivity.INTENT_EXTRA_HEADER_TEXT, getString(R.string.barcode_scanner_text));
                startActivityForResult(intent, REQUEST_CODE_FOR_BARCODE_SCANNING);
            }
        });

        scanReceiptButton = (Button) findViewById(R.id.scan_receipt);
        scanReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, ReceiptScanEntryActivity.class), REQUEST_CODE_FOR_RECEIPT_SCANNING);

            }
        });

        productsListView = (ListView) findViewById(R.id.product_list);
        listHeaderText = LayoutInflater.from(this).inflate(R.layout.listview_header, null);

        if (getIntent().hasExtra(INTENT_BARCODE)) {
            boolean openBarcode = getIntent().getBooleanExtra(INTENT_BARCODE, false);
            if (openBarcode) {
                Intent intent = new Intent(MainActivity.this, BarCodeScannerActivity.class);
                intent.putExtra(BarCodeScannerActivity.INTENT_EXTRA_HEADER_TEXT, getString(R.string.barcode_scanner_text));
                startActivityForResult(intent, REQUEST_CODE_FOR_BARCODE_SCANNING);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cart:
                startActivity(new Intent(MainActivity.this, ShoppingCartActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
                if (searchTerm != null) {
                    handleResponse(new ArrayList<ProductModel>());

                    searchProducts(searchTerm);
                    productsListView.addHeaderView(listHeaderText);
                }
            }
        }
    }

    private void searchProducts(String searchTerm) {
        Call<BarcodeSearchResponse> searchResults = hackdayService.getSearchResults(searchTerm);
        searchResults.enqueue(new Callback<BarcodeSearchResponse>() {
            @Override
            public void onResponse(Call<BarcodeSearchResponse> call, Response<BarcodeSearchResponse> response) {
                handleResponse(response.body().getProducts());
            }

            @Override
            public void onFailure(Call<BarcodeSearchResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error getting Product, please try again\n" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleResponse(List<ProductModel> products) {
        ProductListAdapter adapter = new ProductListAdapter(products, this, true, true);
        productsListView.setAdapter(adapter);
    }
}
