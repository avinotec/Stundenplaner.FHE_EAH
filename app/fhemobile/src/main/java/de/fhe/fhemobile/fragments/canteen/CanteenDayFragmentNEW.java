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
import de.fhe.fhemobile.utils.Define;
import de.fhe.fhemobile.views.canteen.CanteenDayViewNEW;
import de.fhe.fhemobile.vos.canteen.CanteenDayVo;


public class CanteenDayFragmentNEW extends FeatureFragment {

    public static final String TAG = CanteenDayFragmentNEW.class.getSimpleName();


    public CanteenDayFragmentNEW() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link CanteenDayFragmentNEW}
     */
    public static CanteenDayFragmentNEW newInstance(CanteenDayVo _CanteenDay) {
        final CanteenDayFragmentNEW fragment = new CanteenDayFragmentNEW();
        final Bundle args = new Bundle();
        args.putParcelable(Define.Canteen.PARAM_CANTEEN_DAY, _CanteenDay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mCanteenDay = getArguments().getParcelable(Define.Canteen.PARAM_CANTEEN_DAY);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (CanteenDayViewNEW) inflater.inflate(R.layout.fragment_canteen_day, container, false);
        mView.initializeView(mCanteenDay);
        return mView;
    }

    private CanteenDayViewNEW mView;
    private CanteenDayVo mCanteenDay;
}
