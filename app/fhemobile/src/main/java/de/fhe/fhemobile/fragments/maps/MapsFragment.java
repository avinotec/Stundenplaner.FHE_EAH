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

package de.fhe.fhemobile.fragments.maps;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.MapsSingleActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.views.maps.MapsView;

public class MapsFragment extends FeatureFragment {

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (MapsView) inflater.inflate(R.layout.fragment_maps, container, false);

        mView.initializeView(mViewListener);

        return mView;
    }

    private final MapsView.ViewListener mViewListener = new MapsView.ViewListener() {
        @Override
        public void onMapItemClicked(Integer _Position) {
            Intent intent = new Intent(getActivity(), MapsSingleActivity.class);
            intent.putExtra(MapsSingleActivity.EXTRA_MAP_ID, _Position);
            startActivity(intent);
        }
    };

    private MapsView      mView;
}
