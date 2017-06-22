package com.flipkart.smartgrocery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.flipkart.smartgrocery.R;


public class ReceiptScanEntryActivity extends AppCompatActivity {

    private Button scanReceiptButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_entry_point);

        scanReceiptButton = (Button) findViewById(R.id.scan_receipt);
        scanReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanReceiptButton = (Button) findViewById(R.id.scan_receipt);
                scanReceiptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ReceiptScanEntryActivity.this, OCRActivity.class));
                        finish();
                    }
                });
            }
        });
    }
}
