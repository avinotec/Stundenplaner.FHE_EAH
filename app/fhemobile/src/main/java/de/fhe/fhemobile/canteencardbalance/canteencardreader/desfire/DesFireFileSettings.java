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

package de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;

import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.util.ArrayUtils;
import de.fhe.fhemobile.canteencardbalance.canteencardreader.desfire.util.DesFireUtils;

/**
 * Class holding properties for reading NFC Tags with MIFARE DESFire technology
 */
public abstract class DesFireFileSettings implements Parcelable {
    private final byte fileType;
    private final byte commSetting;
    private final byte[] accessRights;

    /* DesfireFile Types */
    private static final byte STANDARD_DATA_FILE = (byte) 0x00;
    private static final byte BACKUP_DATA_FILE = (byte) 0x01;
    private static final byte VALUE_FILE = (byte) 0x02;
    private static final byte LINEAR_RECORD_FILE = (byte) 0x03;
    private static final byte CYCLIC_RECORD_FILE = (byte) 0x04;

    static DesFireFileSettings create(byte[] data) throws DesFireException {
        byte fileType = data[0];

        ByteArrayInputStream stream = new ByteArrayInputStream(data);

        if (fileType == STANDARD_DATA_FILE || fileType == BACKUP_DATA_FILE)
            return new StandardDesFireFileSettings(stream);
        else if (fileType == LINEAR_RECORD_FILE || fileType == CYCLIC_RECORD_FILE)
            return new RecordDesFireFileSettings(stream);
        else if (fileType == VALUE_FILE)
            return new ValueDesFireFileSettings(stream);
        else
            throw new DesFireException("Unknown file type: " + Integer.toHexString(fileType));
    }

    private DesFireFileSettings(ByteArrayInputStream stream) {
        fileType = (byte) stream.read();
        commSetting = (byte) stream.read();

        accessRights = new byte[2];
        //noinspection ResultOfMethodCallIgnored
        stream.read(accessRights, 0, accessRights.length);
    }

    private DesFireFileSettings(byte fileType, byte commSetting, byte[] accessRights) {
        this.fileType = fileType;
        this.commSetting = commSetting;
        this.accessRights = accessRights;
    }

    public static final Creator<DesFireFileSettings> CREATOR = new Creator<DesFireFileSettings>() {
        public DesFireFileSettings createFromParcel(Parcel source) {
            byte fileType = source.readByte();
            byte commSetting = source.readByte();
            byte[] accessRights = new byte[source.readInt()];
            source.readByteArray(accessRights);

            if (fileType == STANDARD_DATA_FILE || fileType == BACKUP_DATA_FILE) {
                int fileSize = source.readInt();
                return new StandardDesFireFileSettings(fileType, commSetting, accessRights, fileSize);
            }
            else if (fileType == LINEAR_RECORD_FILE || fileType == CYCLIC_RECORD_FILE) {
                int recordSize = source.readInt();
                int maxRecords = source.readInt();
                int curRecords = source.readInt();
                return new RecordDesFireFileSettings(fileType, commSetting, accessRights, recordSize, maxRecords, curRecords);
            }
            else {
                return new UnsupportedDesFireFileSettings(fileType);
            }
        }

        public DesFireFileSettings[] newArray(int size) {
            return new DesFireFileSettings[size];
        }
    };

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeByte(fileType);
        parcel.writeByte(commSetting);
        parcel.writeInt(accessRights.length);
        parcel.writeByteArray(accessRights);
    }

    public int describeContents() {
        return 0;
    }

    private static class StandardDesFireFileSettings extends DesFireFileSettings {
        final int fileSize;

        private StandardDesFireFileSettings(ByteArrayInputStream stream) {
            super(stream);
            byte[] buf = new byte[3];
            //noinspection ResultOfMethodCallIgnored
            stream.read(buf, 0, buf.length);
            ArrayUtils.reverse(buf);
            fileSize = DesFireUtils.byteArrayToInt(buf);
        }

        StandardDesFireFileSettings(byte fileType, byte commSetting, byte[] accessRights, int fileSize) {
            super(fileType, commSetting, accessRights);
            this.fileSize = fileSize;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            super.writeToParcel(parcel, flags);
            parcel.writeInt(fileSize);
        }
    }

    private static class RecordDesFireFileSettings extends DesFireFileSettings {
        final int recordSize;
        final int maxRecords;
        final int curRecords;

        RecordDesFireFileSettings(ByteArrayInputStream stream) {
            super(stream);

            byte[] buf = new byte[3];
            //noinspection ResultOfMethodCallIgnored
            stream.read(buf, 0, buf.length);
            ArrayUtils.reverse(buf);
            recordSize = DesFireUtils.byteArrayToInt(buf);

            buf = new byte[3];
            //noinspection ResultOfMethodCallIgnored
            stream.read(buf, 0, buf.length);
            ArrayUtils.reverse(buf);
            maxRecords = DesFireUtils.byteArrayToInt(buf);

            buf = new byte[3];
            //noinspection ResultOfMethodCallIgnored
            stream.read(buf, 0, buf.length);
            ArrayUtils.reverse(buf);
            curRecords = DesFireUtils.byteArrayToInt(buf);
        }

        RecordDesFireFileSettings(byte fileType, byte commSetting, byte[] accessRights, int recordSize, int maxRecords, int curRecords) {
            super(fileType, commSetting, accessRights);
            this.recordSize = recordSize;
            this.maxRecords = maxRecords;
            this.curRecords = curRecords;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            super.writeToParcel(parcel, flags);
            parcel.writeInt(recordSize);
            parcel.writeInt(maxRecords);
            parcel.writeInt(curRecords);
        }
    }


    public static class ValueDesFireFileSettings extends DesFireFileSettings {
        final int lowerLimit;
        final int upperLimit;
        public final int value;
        final byte limitedCreditEnabled;

        ValueDesFireFileSettings(ByteArrayInputStream stream) {
            super(stream);

            byte[] buf = new byte[4];
            //noinspection ResultOfMethodCallIgnored
            stream.read(buf, 0, buf.length);
            ArrayUtils.reverse(buf);
            lowerLimit = DesFireUtils.byteArrayToInt(buf);

            buf = new byte[4];
            //noinspection ResultOfMethodCallIgnored
            stream.read(buf, 0, buf.length);
            ArrayUtils.reverse(buf);
            upperLimit = DesFireUtils.byteArrayToInt(buf);

            buf = new byte[4];
            //noinspection ResultOfMethodCallIgnored
            stream.read(buf, 0, buf.length);
            ArrayUtils.reverse(buf);
            value = DesFireUtils.byteArrayToInt(buf);


            buf = new byte[1];
            //noinspection ResultOfMethodCallIgnored
            stream.read(buf, 0, buf.length);
            limitedCreditEnabled = buf[0];

            //http://www.skyetek.com/docs/m2/desfire.pdf
            //http://neteril.org/files/M075031_desfire.pdf
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            super.writeToParcel(parcel, flags);
            parcel.writeInt(lowerLimit);
            parcel.writeInt(upperLimit);
            parcel.writeInt(value);
            parcel.writeByte(limitedCreditEnabled);
        }
    }

    private static class UnsupportedDesFireFileSettings extends DesFireFileSettings {
        UnsupportedDesFireFileSettings(byte fileType) {
            super(fileType, Byte.MIN_VALUE, new byte[0]);
        }
    }
}