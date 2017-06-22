package com.flipkart.smartgrocery.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flipkart.smartgrocery.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "BarcodeScannerActivity";
    public static final int CAMERA_PERMISSION_REQUEST = 103;

    public static final String INTENT_EXTRA_SCAN_RESULT = "intent_extra_scan_result";
    public static final String INTENT_EXTRA_HEADER_TEXT = "intent_extra_header_text";

    private ZXingScannerView mScannerView;
    private ImageButton mFlashButton;
    private Bundle mBundle;

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";

    private boolean mDeviceHasFlash = false;
    private boolean mIsFlashOn = false;
    private ArrayList<Integer> mSelectedIndices;

    private boolean noPermissionLayoutInitialized = false;
    private boolean cameraLayoutInitialized = false;
    private boolean cameraStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeNoPermissionLayout();
        if (savedInstanceState != null) {
            mIsFlashOn = savedInstanceState.getBoolean(FLASH_STATE, false);
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS);
        } else {
            mIsFlashOn = false;
            mSelectedIndices = null;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // User has denied the permission at least once. Ask it again.
                // In future we may want to show a permission request rationale here.
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            } else {
                // Asking this permission for the first time.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            initializeCameraLayout();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (!cameraLayoutInitialized) {
                initializeCameraLayout();
            }
            startCamera();
        } else if (!noPermissionLayoutInitialized) {
            initializeNoPermissionLayout();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraStarted) {
            stopCamera();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mIsFlashOn);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeCameraLayout();
                } else {
                    findViewById(R.id.camera_permission_error_layout).setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void initializeNoPermissionLayout() {
        setContentView(R.layout.activity_barcode_scanner_no_permission);
        findViewById(R.id.button_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = BarCodeScannerActivity.this;
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
        noPermissionLayoutInitialized = true;
        cameraLayoutInitialized = false;
    }

    private void initializeCameraLayout() {
        setContentView(R.layout.activity_barcode_scanner);
        initializeLayout();
        setupFormats();

        Intent intent = getIntent();
        mBundle = intent.getExtras();
        if (mBundle == null) {
            mBundle = new Bundle();
        }
        String headerText = mBundle.getString(INTENT_EXTRA_HEADER_TEXT, null);
        if (headerText == null) {
            findViewById(R.id.textView_header_barcode_scanner).setVisibility(View.GONE);
        } else {
            TextView headerTextView = (TextView) findViewById(R.id.textView_header_barcode_scanner);
            headerTextView.setText(headerText);
            headerTextView.setVisibility(View.VISIBLE);
        }
        noPermissionLayoutInitialized = false;
        cameraLayoutInitialized = true;
    }

    private void startCamera() {
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(mIsFlashOn);
        mFlashButton.setImageResource(
                mIsFlashOn ? R.drawable.ic_flash_on_white_24dp : R.drawable.ic_flash_off_white_24dp
        );
        cameraStarted = true;
    }

    private void stopCamera() {
        mScannerView.stopCamera();
        cameraStarted = false;
    }

    private void initializeLayout() {
        mScannerView = (ZXingScannerView) findViewById(R.id.zxing_scanner_view);
        mFlashButton = (ImageButton) findViewById(R.id.button_camera_flash);

        mDeviceHasFlash = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!mDeviceHasFlash) {
            mIsFlashOn = false;
            mFlashButton.setVisibility(View.GONE);
        } else {
            mFlashButton.setVisibility(View.VISIBLE);
            mFlashButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsFlashOn = !mIsFlashOn;
                    mScannerView.setFlash(mIsFlashOn);
                    mFlashButton.setImageResource(
                            mIsFlashOn ? R.drawable.ic_flash_on_white_24dp : R.drawable.ic_flash_off_white_24dp
                    );
                }
            });
        }
    }

    private void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }

        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Log.v(TAG, rawResult.getText()); // Prints scan results
        // Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        mBundle.putString(INTENT_EXTRA_SCAN_RESULT, rawResult.getText());
        Intent i = new Intent();
        i.putExtras(mBundle);
        setResult(RESULT_OK, i);
        finish();
    }
}
