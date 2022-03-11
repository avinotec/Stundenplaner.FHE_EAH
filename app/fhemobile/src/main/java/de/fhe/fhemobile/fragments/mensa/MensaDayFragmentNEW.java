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

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.SettingsActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.fragments.timetable.TimeTableWeekFragment;
import de.fhe.fhemobile.models.mensa.MensaFoodModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.utils.feature.Features;
import de.fhe.fhemobile.views.mensa.MensaDayViewNEW;
import de.fhe.fhemobile.views.mensa.MensaView;
import de.fhe.fhemobile.views.mensa.MensaViewNEW;
import de.fhe.fhemobile.vos.mensa.MensaDayVo;


public class MensaDayFragmentNEW extends FeatureFragment {

    public static final String TAG = MensaDayFragmentNEW.class.getSimpleName();


    public MensaDayFragmentNEW() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link MensaDayFragmentNEW}
     */
    public static MensaDayFragmentNEW newInstance(MensaDayVo _MensaDay) {
        final MensaDayFragmentNEW fragment = new MensaDayFragmentNEW();
        final Bundle args = new Bundle();
        args.putParcelable(Define.Mensa.PARAM_MENSA_DAY, _MensaDay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mMensaDay = getArguments().getParcelable(Define.Mensa.PARAM_MENSA_DAY);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MensaDayViewNEW) inflater.inflate(R.layout.fragment_mensa_day, container, false);
        mView.initializeView(mMensaDay);
        return mView;
    }

    private MensaDayViewNEW mView;
    private MensaDayVo mMensaDay;
}
