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
package de.fhe.fhemobile.vos.mensa;

import com.google.gson.annotations.SerializedName;


/**
 * Created by paul on 13.02.14.
 */
public class MensaChoiceItemVo {

    public Integer getId() {
        return mId;
    }

    public void setId(Integer mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

// --Commented out by Inspection START (02.11.2021 17:10):
//    public String getUrlPath() {
//        return mUrlPath;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:10)

// --Commented out by Inspection START (02.11.2021 17:10):
//    public void setUrlPath(String mUrl) {
//        this.mUrlPath = mUrl;
//    }
// --Commented out by Inspection STOP (02.11.2021 17:10)

    @SerializedName("id")
    private Integer mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("urlPath")
    private String mUrlPath;

}
