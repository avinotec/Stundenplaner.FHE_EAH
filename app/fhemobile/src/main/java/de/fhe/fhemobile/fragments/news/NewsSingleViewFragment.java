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

package de.fhe.fhemobile.fragments.news;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.views.news.NewsSingleView;
import de.fhe.fhemobile.vos.news.NewsItemVo;

public class NewsSingleViewFragment extends Fragment {

    public NewsSingleViewFragment() {
        // Required empty public constructor
    }

    public static NewsSingleViewFragment newInstance(final NewsItemVo _NewsItem) {
        final NewsSingleViewFragment fragment = new NewsSingleViewFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_NEWS_ITEM, _NewsItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsItem = getArguments().getParcelable(ARG_NEWS_ITEM);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (NewsSingleView) inflater.inflate(R.layout.fragment_news_single_view, container, false);

        mView.setTitle(mNewsItem.getTitle());
        mView.setText(mNewsItem.getEncoded());
        mView.setAuthor(mNewsItem.getAuthor());
        mView.setPubDate(mNewsItem.getPubDate());
        mView.setCategories("");

        return mView;
    }

    private static final String ARG_NEWS_ITEM = "argNewsItem";

    private NewsSingleView mView;

    private NewsItemVo mNewsItem;

}
