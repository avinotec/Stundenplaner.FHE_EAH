/*
 * Copyright (C) 2011 Eric Butler
 * Copyright (C) 2016 Heinrich Reimer
 *
 * Authors:
 * Eric Butler <eric@codebutler.com>
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

package de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.util;

import androidx.annotation.Nullable;

import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.DesFireException;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.DesFireFileSettings;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.DesFireProtocol;

public class DesFireUtils {

    private DesFireUtils() {
    }

    public static int byteArrayToInt(final byte[] b) {
        return byteArrayToInt(b, 0);
    }

    private static int byteArrayToInt(final byte[] b, final int offset) {
        return byteArrayToInt(b, offset, b.length);
    }

    private static int byteArrayToInt(final byte[] b, final int offset, final int length) {
        return (int) byteArrayToLong(b, offset, length);
    }

    private static long byteArrayToLong(final byte[] b, final int offset, final int length) {
        if (b.length < length)
            throw new IllegalArgumentException("length must be less than or equal to b.length");

        long value = 0;
        for (int i = 0; i < length; i++) {
            final int shift = (length - 1 - i) * 8;
            value += (long) (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    @Nullable
    public static DesFireFileSettings selectAppFile(final DesFireProtocol tag, final int appID, final int fileID) {
        try {
            tag.selectApp(appID);
        } catch (final DesFireException e) {
            return null;
        }
        try {
            return tag.getFileSettings(fileID);
        } catch (final DesFireException e) {
            return null;
        }
    }
}
