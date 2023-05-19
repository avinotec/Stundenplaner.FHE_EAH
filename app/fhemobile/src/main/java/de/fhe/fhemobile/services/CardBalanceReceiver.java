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
package de.fhe.fhemobile.services;


import android.content.Context;
import android.util.Log;

import com.heinrichreimer.canteenbalance.app.AbstractCardBalanceReceiver;
import com.heinrichreimer.canteenbalance.cardreader.CardBalance;

import de.fhe.fhemobile.utils.Utils;

/**
 * Broadcast Receiver
 *
 * Created by Nadja - 05/2023
 */
public class CardBalanceReceiver extends AbstractCardBalanceReceiver {

    @Override
    protected void onReceiveCardBalance(Context context, CardBalance balance) {
        Log.d("CardBalanceReceiver", balance.getBalance());
        Utils.showToast(balance.getBalance() + " €");

    }
}
