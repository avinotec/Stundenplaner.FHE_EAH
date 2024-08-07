/*
 *  Copyright (c) 2014-2022 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.vos.news;

import com.google.gson.annotations.SerializedName;

/**
 * Created by paul on 03.03.15.
 */
public class NewsChannelVo {

    public NewsChannelVo() {
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String _title) {
        mTitle = _title;
    }

    public NewsItemVo[] getNewsItems() {
        return mNewsItems;
    }

    public void setNewsItems(final NewsItemVo[] _newsItems) {
        mNewsItems = _newsItems;
    }

    @SerializedName("title")
    private String mTitle;

    @SerializedName("item")
    private NewsItemVo[] mNewsItems;
}
