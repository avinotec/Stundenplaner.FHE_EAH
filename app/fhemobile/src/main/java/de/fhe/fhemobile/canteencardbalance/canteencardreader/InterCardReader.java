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

import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import java.io.IOException;
import java.math.BigDecimal;

import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.DesfireException;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.DesfireFileSettings;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.DesfireProtocol;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.util.DesfireUtils;

/**
 * Reader for cards of the type InterCard
 */
public class InterCardReader {

	private static InterCardReader instance;
	private static final BigDecimal THOUSAND = new BigDecimal(1000);


	public static InterCardReader getInstance() {
		if (instance == null){
			instance = new InterCardReader();
		}
		return instance;
	}

	/**
	 * Try to read data from a card.
	 * <p>
	 * An implementer should only throw exceptions on communication errors, but not because the card
	 * does not contain the required data. In that case, null should be returned.
	 *
	 * @param card The card to read
	 * @return Card's data, null if unsupported.
	 * @throws DesfireException Communication error
	 */
	public CardBalance readCard(DesfireProtocol card) throws DesfireException {

		final int appId = 0x5F8415;
		final int fileId = 1;
		// Selecting app and file
		DesfireFileSettings settings = DesfireUtils.selectAppFile(card, appId, fileId);

		if (settings instanceof DesfireFileSettings.ValueDesfireFileSettings) {
			// Found value file

            // Last transaction in tenths of Euro cents
			int lastTransactionTenthsOfCents = ((DesfireFileSettings.ValueDesfireFileSettings) settings).value;

            // Last transaction in Euro
            BigDecimal lastTransaction = new BigDecimal(lastTransactionTenthsOfCents)
                    .divide(THOUSAND, 4, BigDecimal.ROUND_HALF_UP);

			// Reading value
			try {
				// Balance in tenths of Euro cents
				int balanceTenthsOfCents = card.readValue(fileId);

				// Balance in Euro
				BigDecimal balance = new BigDecimal(balanceTenthsOfCents)
						.divide(THOUSAND, 4, BigDecimal.ROUND_HALF_UP);


				return new CardBalance(balance, lastTransaction);
			} catch (Exception e) {
				return null;
			}

		}
		else {
			// File is not a value file, tag is incompatible
			return null;
		}
	}

	public CardBalance readTag(Tag tag) throws DesfireException {
		// Loading tag
		IsoDep tech = IsoDep.get(tag);

		try {
			tech.connect();
		} catch (IOException e) {
			//Tag was removed. We fail silently.
			e.printStackTrace();
			return null;
		}

		try {
			DesfireProtocol desfireTag = new DesfireProtocol(tech);


			//Android has a Bug on Devices using a Broadcom NFC chip. See
			// http://code.google.com/p/android/issues/detail?id=58773
			//A Workaround is to connected to the tag, issue a dummy operation and then reconnect...
			try {
				desfireTag.selectApp(0);
			} catch (ArrayIndexOutOfBoundsException e) {
				//Exception occurs because the actual response is shorter than the error response
			}

			tech.close();
			tech.connect();

			return InterCardReader.getInstance().readCard(desfireTag);


		} catch (IOException e) {
			//This can only happen on tag close. we ignore this.
			e.printStackTrace();
			return null;
		} finally {
			if (tech.isConnected())
				try {
					tech.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}
}
