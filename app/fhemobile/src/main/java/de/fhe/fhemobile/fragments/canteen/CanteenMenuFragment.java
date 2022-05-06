/*
 *  Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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

package de.fhe.fhemobile.fragments.canteen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import org.junit.Assert;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.views.canteen.CanteenMenuView;

/**
 * Edited by Nadja - 05.2022
 */
public class CanteenMenuFragment extends FeatureFragment {

    private final String TAG = CanteenMenuFragment.class.getSimpleName();

    public static final String PARAM_CANTEEN_ID = "paramCanteenId"; //$NON-NLS


    public CanteenMenuFragment(){
        // Required empty public constructor
    }

    /**
     * Construct a new {@link CanteenMenuFragment} instance
     * @param _CanteenId The ID of the canteen that's menu is shown in this fragment
     */
    public static CanteenMenuFragment newInstance(String _CanteenId) {
        final CanteenMenuFragment fragment = new CanteenMenuFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_CANTEEN_ID, _CanteenId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) Assert.assertNotNull(getArguments());
        this.mCanteenId = getArguments().getString(PARAM_CANTEEN_ID);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = (CanteenMenuView) inflater.inflate(R.layout.fragment_canteen_menu, container, false);

        if(BuildConfig.DEBUG) Assert.assertNotNull(mCanteenId);
        mView.initializeView(mCanteenId);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.registerModelListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.deregisterModelListener();
    }


    private CanteenMenuView mView;
    private String mCanteenId;

}
