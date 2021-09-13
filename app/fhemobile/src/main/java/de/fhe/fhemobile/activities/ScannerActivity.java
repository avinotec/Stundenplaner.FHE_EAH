package de.fhe.fhemobile.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    //Constants
    private static final String TAG = "ScannerActivity"; //$NON-NLS

    private static final int REQUEST_CODE_CAMERA = 123;

    //Variables
    private ZXingScannerView mScannerView;
    private boolean mAutoFocus = true;
    private String destinationQRCode;
    private String startLocation;
    private boolean skipScanner;
    private ArrayList<String> availableRooms;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get extra from parent
        Intent intendScannerActivity = getIntent();
        destinationQRCode = intendScannerActivity.getStringExtra("destinationLocation");
        startLocation = intendScannerActivity.getStringExtra("startLocation");
        availableRooms = intendScannerActivity.getStringArrayListExtra("availableRooms");

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setAutoFocus(mAutoFocus);

        //Check necessary permissions
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        if (!skipScanner) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
                mScannerView.setAutoFocus(mAutoFocus);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPause() {
        super.onPause();
        if (!skipScanner) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                mScannerView.stopCamera();
            }
        }
    }

    @Override
    public void handleResult(Result rawResult) {

        startLocation = rawResult.getText();
        ArrayList<String> availableRoomsQRCodes = new ArrayList<>();

        try {
            //Get list of all valid qr-codes
            for (int index = 0; index < availableRooms.size(); index++) {
                String roomToQRCode = availableRooms.get(index).replaceAll("\\.", "");
                availableRoomsQRCodes.add(roomToQRCode);
            }

            //If QR-Code is valid -> intent
            if (availableRoomsQRCodes.contains(startLocation)) {
                Intent intentNavigationActivity = new Intent(getApplicationContext(), NavigationActivity.class);
                intentNavigationActivity.putExtra("startLocation", startLocation);
                intentNavigationActivity.putExtra("destinationLocation", destinationQRCode);
                startActivity(intentNavigationActivity);
            }

            //If QR-Code is invalid -> restart scan
            if(!availableRoomsQRCodes.contains(startLocation)) {
                mScannerView.stopCameraPreview();
                mScannerView.stopCamera();
                mScannerView.startCamera();
                mScannerView.resumeCameraPreview(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "intend exception", e);

        }
    }
}
