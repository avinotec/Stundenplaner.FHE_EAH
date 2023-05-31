/*
 *  Copyright (c) 2023-2023 Ernst-Abbe-Hochschule Jena
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

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.CardBalance;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.canteencardbalance.CardBalanceReceiver;
import de.fhe.fhemobile.views.canteen.CanteenCardBalanceView;

/**
 * Fragment for showing the balance of a canteen card detected via NFC
 *
 * Created by Nadja - 18.05.2023
 */
public class CanteenCardBalanceFragment extends FeatureFragment {

    public CanteenCardBalanceFragment(){
    }

    public static CanteenCardBalanceFragment newInstance(){
        final CanteenCardBalanceFragment fragment = new CanteenCardBalanceFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        CanteenCardBalanceView mView = (CanteenCardBalanceView) inflater.inflate(R.layout.fragment_canteencardbalance, container, false);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //register canteen card balance receiver
        getContext().registerReceiver(new CardBalanceReceiver(), new IntentFilter(CardBalance.ACTION_CARD_BALANCE));
    }
}
