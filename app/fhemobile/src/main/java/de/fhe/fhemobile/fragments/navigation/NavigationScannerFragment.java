/*
 *  Copyright (c) 2022-2023 Ernst-Abbe-Hochschule Jena
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package de.fhe.fhemobile.fragments.navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;

import com.google.zxing.Result;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.utils.Define;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Fragment holds an QR code scanner to scan a room's code
 *
 * created by Nadja - 03/2022
 */
public class NavigationScannerFragment extends FeatureFragment implements ZXingScannerView.ResultHandler{

    public static final String TAG = NavigationScannerFragment.class.getSimpleName();

    private ZXingScannerView mScannerView;
    private boolean mAutoFocus = true;

    public NavigationScannerFragment() {
        super(TAG);
    }


    public static NavigationScannerFragment newInstance() {
        final NavigationScannerFragment fragment = new NavigationScannerFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
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
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mScannerView = new ZXingScannerView(getContext());
        mScannerView.setAutoFocus(mAutoFocus);

        return mScannerView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
        // see https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
        final MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {
                // Add menu items here
                menu.clear();
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
                // Handle the menu selection
                return false;
            }
        });
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
    public void handleResult(final Result result) {
        final String rawText = result.getText();

        //cut away number for different room entries (e.g. "/1")
        String room = rawText.split("/")[0];
        //if necessary, correct floor 03 to 3Z
        if(room.matches("05\\.03\\.\\d{3}(/\\d)?")){
            final String[] array = room.split("\\.");
            room = array[0] +".3Z."+ array[2];
        }


        final Bundle bundle = new Bundle();
        bundle.putString(Define.Navigation.KEY_SCANNED_ROOM, room);
        getActivity().getSupportFragmentManager().setFragmentResult(Define.Navigation.REQUEST_SCANNED_START_ROOM, bundle);

        getActivity().onBackPressed();
    }



    private final ActivityResultLauncher<String> requestPermissionLauncher =
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
                    //Todo?
                }
            });
}
