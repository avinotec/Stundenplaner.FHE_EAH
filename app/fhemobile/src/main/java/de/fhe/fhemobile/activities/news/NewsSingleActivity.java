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
package de.fhe.fhemobile.activities.news;

import android.content.Intent;
import android.os.Bundle;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.SecondaryActivity;
import de.fhe.fhemobile.fragments.news.NewsSingleViewFragment;
import de.fhe.fhemobile.vos.news.NewsItemVo;

public class NewsSingleActivity extends SecondaryActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(R.layout.activity_news_single);

        final Intent intent = getIntent();
        final NewsItemVo newsItem = intent.getParcelableExtra(EXTRA_NEWS_ITEM);

        setFragment(NewsSingleViewFragment.newInstance(newsItem));
    }

    public static final String EXTRA_NEWS_ITEM   = "extraNewsItem";

}
