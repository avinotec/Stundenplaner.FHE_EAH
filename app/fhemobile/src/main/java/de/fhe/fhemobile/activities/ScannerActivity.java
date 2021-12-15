//package de.fhe.fhemobile.activities;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.zxing.Result;
//
//import java.util.ArrayList;
//
//import me.dm7.barcodescanner.zxing.ZXingScannerView;
//
///**
// *  Activity for getting the users current position via QR code scanner
// *  source: Bachelor Thesis from Tim Münziger from SS2020
// *  edit and integration: Nadja 09.2021
// */
//public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
//
//    //Constants
//    private static final String TAG = "ScannerActivity"; //$NON-NLS
//
//    private static final int REQUEST_CODE_CAMERA = 123;
//
//    //Variables
//    private ZXingScannerView mScannerView;
//    private boolean mAutoFocus = true;
//    private String destinationQRCode;
//    private String startLocation;
//    private ArrayList<String> availableRooms;
//    //for sending already read in rooms json string to navigation activity
//    private  String roomsJSON;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //Get extra from parent
//        Intent intendScannerActivity = getIntent();
//        destinationQRCode = intendScannerActivity.getStringExtra("destinationLocation");
//        availableRooms = intendScannerActivity.getStringArrayListExtra("availableRooms");
//        roomsJSON = intendScannerActivity.getStringExtra("rooms");
//
//        mScannerView = new ZXingScannerView(this);
//        setContentView(mScannerView);
//        mScannerView.setAutoFocus(mAutoFocus);
//
//        //Check necessary permissions
//        //android developer documentation: "For apps targeting API lower than Build.VERSION_CODES.M these permissions are always granted as such apps do not expect permission revocations and would crash."
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
//            }
//        }
//
//    }
//
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        //android developer documentation: "For apps targeting API lower than Build.VERSION_CODES.M these permissions are always granted as such apps do not expect permission revocations and would crash."
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                mScannerView.setResultHandler(this);
//                mScannerView.startCamera();
//                mScannerView.setAutoFocus(mAutoFocus);
//            }
//        }
//    }
//
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        //android developer documentation: "For apps targeting API lower than Build.VERSION_CODES.M these permissions are always granted as such apps do not expect permission revocations and would crash."
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                mScannerView.stopCamera();
//            }
//        }
//    }
//
//    @Override
//    public void handleResult(Result rawResult) {
//
//        startLocation = rawResult.getText();
//        ArrayList<String> availableRoomsQRCodes = new ArrayList<>();
//
//        try {
//            //Get list of all valid qr-codes
//            for (int index = 0; index < availableRooms.size(); index++) {
//                String roomToQRCode = availableRooms.get(index).replaceAll("\\.", "");
//                availableRoomsQRCodes.add(roomToQRCode);
//            }
//
//            //If QR-Code is valid -> intent
//            if (availableRoomsQRCodes.contains(startLocation)) {
//                Intent intentNavigationActivity = new Intent(getApplicationContext(), NavigationActivityOLD.class);
//                intentNavigationActivity.putExtra("startLocation", startLocation);
//                intentNavigationActivity.putExtra("destinationLocation", destinationQRCode);
//                intentNavigationActivity.putExtra("rooms", roomsJSON);
//                startActivity(intentNavigationActivity);
//            }
//
//            //If QR-Code is invalid -> restart scan
//            if(!availableRoomsQRCodes.contains(startLocation)) {
//                mScannerView.stopCameraPreview();
//                mScannerView.stopCamera();
//                mScannerView.startCamera();
//                mScannerView.resumeCameraPreview(this);
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "intend exception", e);
//
//        }
//    }
//}
