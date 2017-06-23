package com.flipkart.smartgrocery.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flipkart.smartgrocery.R;
import com.flipkart.smartgrocery.adapters.ProductListAdapter;
import com.flipkart.smartgrocery.netowking.HackdayService;
import com.flipkart.smartgrocery.netowking.RetrofitApiClient;
import com.flipkart.smartgrocery.netowking.response.ReceiptScanResponse;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by satyanarayana.p on 22/06/17.
 */

public class OCRActivity extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;

    private static final String TAG = OCRActivity.class.getSimpleName();

    public static final int CAMERA_PERMISSION_REQUEST = 103;

    private Uri imageUri;
    private TextView detectedTextView;
    private Button scan;
    private TextView yourCostView;
    private TextView fkCostView;
    private TextView savingsView;
    private ListView productsListView;
    private Button continueShopping;

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";

    private boolean noPermissionLayoutInitialized = false;
    private boolean cameraLayoutInitialized = false;
    private boolean cameraStarted = false;
    private ArrayList<Integer> mSelectedIndices;
    private boolean mDeviceHasFlash = false;
    private boolean mIsFlashOn = false;

    private HackdayService hackdayService = RetrofitApiClient.getClient().create(HackdayService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeNoPermissionLayout();
        setContentView(R.layout.activity_ocr);
        if (savedInstanceState != null) {
            mIsFlashOn = savedInstanceState.getBoolean(FLASH_STATE, false);
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS);
        } else {
            mIsFlashOn = false;
            mSelectedIndices = null;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // User has denied the permission at least once. Ask it again.
                // In future we may want to show a permission request rationale here.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // User has denied the permission at least once. Ask it again.
                // In future we may want to show a permission request rationale here.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
            } else {
                // Asking this permission for the first time.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            takePhoto();
        }

        yourCostView = (TextView) findViewById(R.id.your_cost);
        fkCostView = (TextView) findViewById(R.id.fk_cost);
        savingsView = (TextView) findViewById(R.id.savings);
        productsListView = (ListView) findViewById(R.id.product_list);
        continueShopping = (Button) findViewById(R.id.continueToCart);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OCRActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
            }
        });

        detectedTextView = (TextView) findViewById(R.id.detected_text);
        detectedTextView.setMovementMethod(new ScrollingMovementMethod());
        scan = (Button) findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
    }

    private void takePhoto() {
        String filename = System.currentTimeMillis() + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void initializeNoPermissionLayout() {
        setContentView(R.layout.activity_barcode_scanner_no_permission);
        findViewById(R.id.button_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = OCRActivity.this;
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

    private void inspectFromBitmap(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue().trim());
                    detectedText.append("\n");
                }
            }

            sendReceiptOCRData(detectedText.toString().trim());

            saveDataInPrefs(detectedText.toString().trim());
            Toast.makeText(OCRActivity.this, detectedText, Toast.LENGTH_LONG).show();

        } finally {
            textRecognizer.release();
        }
    }

    private void saveDataInPrefs(String detectedText) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_file_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_last_detected_text), detectedText);
        editor.apply();
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
                Toast.makeText(OCRActivity.this, "Error getting products, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleReceiptScanResponse(ReceiptScanResponse response) {
        yourCostView.setText(response.getYourCost());
        fkCostView.setText(response.getFkCost());
        savingsView.setText(response.getSaving());
        ProductListAdapter adapter = new ProductListAdapter(response.getProducts(), this);
        productsListView.setAdapter(adapter);
    }

    private void inspect(Uri uri) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            inspectFromBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Failed to find the file: " + uri, e);
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.w(TAG, "Failed to close InputStream", e);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    inspect(data.getData());
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (imageUri != null) {
                        inspect(imageUri);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //initializeCameraLayout();
                } else {
                    findViewById(R.id.camera_permission_error_layout).setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
