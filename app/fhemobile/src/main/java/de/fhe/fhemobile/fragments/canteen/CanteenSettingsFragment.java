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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.canteen.CanteenModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.views.canteen.CanteenSettingsView;
import de.fhe.fhemobile.vos.canteen.CanteenVo;


public class CanteenSettingsFragment extends FeatureFragment {

    public static final String TAG = "CanteenSettingsFragment"; //$NON-NLS

    public CanteenSettingsFragment() {
        // Required empty public constructor
    }

    public static CanteenSettingsFragment newInstance() {
        final CanteenSettingsFragment fragment = new CanteenSettingsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (CanteenSettingsView) inflater.inflate(R.layout.fragment_canteen_settings, container, false);
        mView.initializeView(mViewListener);

        if(CanteenModel.getInstance().getCanteens() == null) {
            NetworkHandler.getInstance().fetchAvailableCanteens();
        }
        else {
            mView.initCanteenSelectionListView();
        }

        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            mView.destroy();
        }
    }


    private final CanteenSettingsView.ViewListener mViewListener = new CanteenSettingsView.ViewListener() {
        @Override
        public void onCanteenSelected(final Integer _Id, final Integer _Position) {

            //id has been used as position
            final CanteenVo canteen = CanteenModel.getInstance().getCanteens()[_Position];

            UserSettings.getInstance().addOrRemoveFromSelectedCanteens(canteen);
        }
    };

    private CanteenSettingsView mView;

}
