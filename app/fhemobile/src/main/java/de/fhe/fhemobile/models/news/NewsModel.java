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
package de.fhe.fhemobile.models.news;

import de.fhe.fhemobile.events.EventDispatcher;
import de.fhe.fhemobile.events.SimpleEvent;
import de.fhe.fhemobile.utils.UserSettings;
import de.fhe.fhemobile.vos.news.NewsCategoryVo;
import de.fhe.fhemobile.vos.news.NewsItemVo;

/**
 * Created by paul on 01.02.14.
 */
public final class NewsModel extends EventDispatcher {

    public static class NewsChangeEvent extends SimpleEvent {
        public static final String RECEIVED_NEWS = "receivedNews";
        public static final String RECEIVED_EMPTY_NEWS = "receivedEmptyNews";

        public static final String RECEIVED_CATEGORY_ITEMS = "receivedCategoryItems";

        public NewsChangeEvent(final String type) {
            super(type);
        }
    }

    public NewsItemVo[] getNewsItems() {
        return mNewsItems;
    }

    public void setNewsItems(final NewsItemVo[] _NewsItems) {
        mNewsItems = _NewsItems;
        
        if(mNewsItems != null && mNewsItems.length > 0) {
            notifyChange(NewsChangeEvent.RECEIVED_NEWS);
        }
        else {
            notifyChange(NewsChangeEvent.RECEIVED_EMPTY_NEWS);
        }
    }

    public NewsCategoryVo[] getCategoryItems() {
        return mCategoryItems;
    }

    public void setCategoryItems(final NewsCategoryVo[] _CategoryItems) {
        final Integer selectedId = Integer.valueOf(UserSettings.getInstance().getChosenNewsCategory());
        
        for(int i = 0; i < _CategoryItems.length; i++) {
            if(selectedId.equals(_CategoryItems[i].getId())) {
                mChosenNewsItemPosition = i;
                break;
            }
        }

        mCategoryItems = _CategoryItems;

        notifyChange(NewsChangeEvent.RECEIVED_CATEGORY_ITEMS);
    }

    public Integer getChosenNewsItemPosition() {
        return mChosenNewsItemPosition;
    }

    public void setChosenNewsItemPosition(final Integer _ChosenNewsItemPosition) {
        mChosenNewsItemPosition = _ChosenNewsItemPosition;
    }

    public static NewsModel getInstance() {
        if(ourInstance == null) {
            ourInstance = new NewsModel();
        }
        return ourInstance;
    }

    private NewsModel() {
    }

    private void notifyChange(final String type) {
        dispatchEvent(new NewsChangeEvent(type));
    }

    private static NewsModel ourInstance;

    private NewsItemVo[] mNewsItems;
    private NewsCategoryVo[] mCategoryItems;

    private Integer mChosenNewsItemPosition = 0;
}
