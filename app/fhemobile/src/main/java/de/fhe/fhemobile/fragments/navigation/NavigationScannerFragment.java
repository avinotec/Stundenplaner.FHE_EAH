package de.fhe.fhemobile.fragments.navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.utils.Define;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Fragment holds an QR code scanner to scan a room's code
 *
 * created by Nadja - 03/2022
 */
public class NavigationScannerFragment extends FeatureFragment implements ZXingScannerView.ResultHandler{

    public static final String TAG = "NavigationScannerFragment"; //$NON-NLS

    private ZXingScannerView mScannerView;
    private boolean mAutoFocus = true;

    public NavigationScannerFragment() {
        // Required empty public constructor
    }


    public static NavigationScannerFragment newInstance() {
        NavigationScannerFragment fragment = new NavigationScannerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check necessary permissions
        //android developer documentation: "For apps targeting API lower than Build.VERSION_CODES.M these permissions are always granted as such apps do not expect permission revocations and would crash."
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mScannerView = new ZXingScannerView(getContext());
        mScannerView.setAutoFocus(mAutoFocus);

        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //android developer documentation: "For apps targeting API lower than Build.VERSION_CODES.M these permissions are always granted as such apps do not expect permission revocations and would crash."
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
                mScannerView.setAutoFocus(mAutoFocus);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //android developer documentation: "For apps targeting API lower than Build.VERSION_CODES.M these permissions are always granted as such apps do not expect permission revocations and would crash."
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

                mScannerView.stopCamera();
            }
        }
    }

    @Override
    public void handleResult(Result result) {
        String rawText = result.getText();

        //cut away number for different room entries (e.g. "/1")
        String room = rawText.split("/")[0];
        //if necessary, correct floor 03 to 3Z
        if(room.matches("05\\.03\\.\\d{3}(/\\d)?")){
            String[] array = room.split("\\.");
            room = array[0] +".3Z."+ array[2];
        }


        Bundle bundle = new Bundle();
        bundle.putString(Define.Navigation.KEY_SCANNED_ROOM, room);
        getActivity().getSupportFragmentManager().setFragmentResult(Define.Navigation.REQUEST_SCANNED_START_ROOM, bundle);

        getActivity().onBackPressed();
    }



    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
}
