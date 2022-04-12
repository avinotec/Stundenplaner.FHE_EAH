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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MainActivity;
import de.fhe.fhemobile.activities.SettingsActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.utils.feature.Features;
import de.fhe.fhemobile.utils.canteen.CanteenSettings;
import de.fhe.fhemobile.utils.canteen.CanteenUtils;
import de.fhe.fhemobile.views.canteen.CanteenViewNEW;
import de.fhe.fhemobile.vos.canteen.CanteenChoiceItemVo;
import de.fhe.fhemobile.vos.canteen.CanteenFoodItemVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CanteenFragmentNEW#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Created by Nadja in 03/2022
 */
public class CanteenFragmentNEW extends FeatureFragment {

    public static final String TAG = CanteenFragmentNEW.class.getSimpleName(); //$NON-NLS

    public CanteenFragmentNEW() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link CanteenFragmentNEW}.
     */
    public static CanteenFragmentNEW newInstance() {
        final CanteenFragmentNEW fragment = new CanteenFragmentNEW();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mChosenCanteenId = getArguments().getString(Define.Canteen.PARAM_CANTEEN_ID);
        } else {
            NetworkHandler.getInstance().fetchAvailableCanteens(mCallbackCanteens);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (CanteenViewNEW) inflater.inflate(R.layout.fragment_canteen, container, false);

        mView.initializeView(getChildFragmentManager(), getLifecycle());

        NetworkHandler.getInstance().fetchCanteenItems(mChosenCanteenId, mCallbackMenu);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mChosenCanteenId != null){
            NetworkHandler.getInstance().fetchCanteenItems(mChosenCanteenId, mCallbackMenu);
        } else{
            //fetch available canteens and load menu for first canteen in list
            NetworkHandler.getInstance().fetchAvailableCanteens(mCallbackCanteens);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        menu.clear();
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    //onOptionsItemSelected-------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(final MenuItem _item) {

        if (_item.getItemId() == R.id.action_settings) {
            CanteenSettings.saveCanteenChoice(null);

            //todo
            ((MainActivity) getActivity()).changeFragment(CanteenSettingsFragment.newInstance(), false, CanteenSettingsFragment.TAG);

            final Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, Features.FeatureId.CANTEEN);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    public void onRestoreActionBar(final ActionBar _ActionBar) {
        super.onRestoreActionBar(_ActionBar);
        //todo: update to usage of CanteenSettings
        _ActionBar.setTitle(UserSettings.getInstance().getChosenCanteenName());
    }

    private final Callback<CanteenFoodItemVo[]> mCallbackMenu = new Callback<CanteenFoodItemVo[]>() {
        @Override
        public void onResponse(Call<CanteenFoodItemVo[]> call, Response<CanteenFoodItemVo[]> response) {
            if(response.body() != null){
                mView.setPagerItems(CanteenUtils.groupPerDay(response.body()));
            }
        }

        @Override
        public void onFailure(Call<CanteenFoodItemVo[]> call, Throwable t) {

        }
    };

    private final Callback<CanteenChoiceItemVo[]> mCallbackCanteens = new Callback() {
        @Override
        public void onResponse(Call call, Response response) {
            if(response.body() != null){
                CanteenChoiceItemVo firstCanteenInList = ((CanteenChoiceItemVo[]) response.body())[0];
                mChosenCanteenId = firstCanteenInList.getId().toString();
                NetworkHandler.getInstance().fetchCanteenItems(mChosenCanteenId, mCallbackMenu);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {

        }
    };

    private CanteenViewNEW mView;
    private String mChosenCanteenId;
}
