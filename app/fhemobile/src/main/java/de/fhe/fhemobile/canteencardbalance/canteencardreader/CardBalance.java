/*
 * Copyright (C) 2014 Jakob Wenzel
 * Copyright (C) 2016 Heinrich Reimer
 *
 * Authors:
 * Jakob Wenzel <jakobwenzel92@gmail.com>
 * Heinrich Reimer <heinrich@heinrichreimer.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.fhe.fhemobile.canteencardbalance.canteencardreader;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import de.fhe.fhemobile.utils.Define;

public class CardBalance implements Parcelable {

    private static String CURRENCY = "€";


    private static final DecimalFormat GERMAN_NUMBER_FORMAT =
            (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);

    static {
        GERMAN_NUMBER_FORMAT.setMinimumFractionDigits(2);
        GERMAN_NUMBER_FORMAT.setMaximumFractionDigits(2);
    }


    CardBalance(BigDecimal balance, BigDecimal lastTransaction, Date dateOfStatus) {
        this.mBalance = balance;
        this.mLastTransaction = lastTransaction;
        this.mDateOfStatus = dateOfStatus;
    }

    @Nullable
    public static CardBalance fromBundle(Bundle in) {
        Serializable balance = in.getSerializable(Define.Canteen.KEY_BALANCE);
        Serializable lastTransaction = in.getSerializable(Define.Canteen.KEY_LAST_TRANSACTION);
        Long dateOfStatus = in.getLong(Define.Canteen.KEY_DATE_OF_STATUS);
        if (!(balance instanceof BigDecimal && lastTransaction instanceof BigDecimal)) {
            return null;
        }
        return new CardBalance((BigDecimal) balance, (BigDecimal) lastTransaction,
                dateOfStatus != null ? new Date(dateOfStatus) : null);
    }

    public static void setCurrency(String currency) {
        CardBalance.CURRENCY = currency;
    }

    public static String getCurrency() {
        return CardBalance.CURRENCY;
    }

    /**
     * Get the mBalance in format xx,xx
     * @return
     */
    public String getBalance() {
        return GERMAN_NUMBER_FORMAT.format(mBalance);
    }

    /**
     * Get the amount of the last transaction
     * @return
     */
    public String getLastTransaction() {
        return mLastTransaction == null ? "" : GERMAN_NUMBER_FORMAT.format(mLastTransaction);
    }

    public boolean isLastTransactionSupported() {
        return mLastTransaction != null;
    }

    public Date getDateOfStatus() {
        return mDateOfStatus;
    }


    @Override
    public String toString() {
        return "Card mBalance: " + getBalance() +
                "€ (Last transaction: " + getLastTransaction() + "€)";
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Define.Canteen.KEY_BALANCE, mBalance);
        bundle.putSerializable(Define.Canteen.KEY_LAST_TRANSACTION, mLastTransaction);
        bundle.putLong(Define.Canteen.KEY_DATE_OF_STATUS, mDateOfStatus.getTime());
        return bundle;
    }

    // ---------------------- PARCELABLE ----------------------------------------

    public CardBalance(Parcel _In){
        mBalance = (BigDecimal) _In.readSerializable();
        mLastTransaction = (BigDecimal) _In.readSerializable();
        mDateOfStatus = new Date(_In.readLong());
    }

    public  static final Parcelable.Creator<CardBalance> CREATOR
            = new Creator<CardBalance>() {
        @Override
        public CardBalance createFromParcel(Parcel source) {
            return new CardBalance(source);
        }

        @Override
        public CardBalance[] newArray(int size) {
            return new CardBalance[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeSerializable(mBalance);
        dest.writeSerializable(mLastTransaction);
        dest.writeLong(mDateOfStatus.getTime());
    }

    // ---------------------- END PARCELABLE ----------------------------------------


    /** Current mBalance on card in Euros or any other currency. */
    private BigDecimal mBalance;

    /**  Amount of the last transaction in Euros or any other currency, null if not supported by card. */
    private BigDecimal mLastTransaction;

    /**
     * Time of when the balance was read
     */
    private Date mDateOfStatus;
}