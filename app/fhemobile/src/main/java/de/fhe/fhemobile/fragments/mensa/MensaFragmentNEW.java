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

package de.fhe.fhemobile.fragments.mensa;

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
import de.fhe.fhemobile.utils.mensa.MensaSettings;
import de.fhe.fhemobile.utils.mensa.MensaUtils;
import de.fhe.fhemobile.views.mensa.MensaViewNEW;
import de.fhe.fhemobile.vos.mensa.MensaChoiceItemVo;
import de.fhe.fhemobile.vos.mensa.MensaFoodItemVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MensaFragmentNEW#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Created by Nadja in 03/2022
 */
public class MensaFragmentNEW extends FeatureFragment {

    public static final String TAG = "MensaFragment"; //$NON-NLS

    public MensaFragmentNEW() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link MensaFragmentNEW}.
     */
    public static MensaFragmentNEW newInstance() {
        final MensaFragmentNEW fragment = new MensaFragmentNEW();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mChosenMensaId = getArguments().getString(Define.Mensa.PARAM_MENSA_ID);
        } else {
            NetworkHandler.getInstance().fetchAvailableMensas(mCallbackMensas);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MensaViewNEW) inflater.inflate(R.layout.fragment_mensa, container, false);

        mView.initializeView(getChildFragmentManager(), getLifecycle());

        NetworkHandler.getInstance().fetchMensaItems(mChosenMensaId, mCallbackMenu);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mChosenMensaId != null){
            NetworkHandler.getInstance().fetchMensaItems(mChosenMensaId, mCallbackMenu);
        } else{
            //fetch available mensas and load menu for first mensa in list
            NetworkHandler.getInstance().fetchAvailableMensas(mCallbackMensas);
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
            MensaSettings.saveMensaChoice(null);

            //todo
            ((MainActivity) getActivity()).changeFragment(MensaSettingsFragment.newInstance(), false, MensaSettingsFragment.TAG);

            final Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, Features.FeatureId.MENSA);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(_item);
    }

    @Override
    public void onRestoreActionBar(final ActionBar _ActionBar) {
        super.onRestoreActionBar(_ActionBar);
        //todo: update to usage of MensaSettings
        _ActionBar.setTitle(UserSettings.getInstance().getChosenMensaName());
    }

    private final Callback<MensaFoodItemVo[]> mCallbackMenu = new Callback<MensaFoodItemVo[]>() {
        @Override
        public void onResponse(Call<MensaFoodItemVo[]> call, Response<MensaFoodItemVo[]> response) {
            if(response.body() != null){
                mView.setPagerItems(MensaUtils.groupPerDay(response.body()));
            }
        }

        @Override
        public void onFailure(Call<MensaFoodItemVo[]> call, Throwable t) {

        }
    };

    private final Callback<MensaChoiceItemVo[]> mCallbackMensas = new Callback() {
        @Override
        public void onResponse(Call call, Response response) {
            if(response.body() != null){
                MensaChoiceItemVo firstMensaInList = ((MensaChoiceItemVo[]) response.body())[0];
                mChosenMensaId = firstMensaInList.getId().toString();
                NetworkHandler.getInstance().fetchMensaItems(mChosenMensaId, mCallbackMenu);
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {

        }
    };

    private MensaViewNEW mView;
    private String mChosenMensaId;
}
