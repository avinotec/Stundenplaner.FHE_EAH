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
package de.fhe.fhemobile.views.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.StickyHeaderAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.utils.headerlistview.HeaderListView;
import de.fhe.fhemobile.vos.news.NewsItemVo;
import de.fhe.fhemobile.widgets.stickyHeaderList.DefaultHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.IHeaderItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.NewsRowItem;
import de.fhe.fhemobile.widgets.stickyHeaderList.WeatherRowItem;

/**
 * Created by paul on 23.01.14.
 */
public class NewsListView extends LinearLayout {

    public interface ViewListener {
        void onNewsItemClick(Integer _Id);
    }

    public NewsListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel   = NewsModel.getInstance();
    }

    public void setViewListener(final ViewListener _Listener) {
        mViewListener = _Listener;
    }

    public void init(final ViewListener _Listener) {
        mViewListener = _Listener;

        if(mModel.getNewsItems() != null) {
            initializeAdapter();
        }
        else {
            mNewsProgressBar.setVisibility(VISIBLE);
        }

    }

    public void registerModelListener() {
        mModel.addListener(NewsModel.ChangeEvent.RECEIVED_NEWS, mReceivedNewsListener);
        mModel.addListener(NewsModel.ChangeEvent.RECEIVED_EMPTY_NEWS, mReceivedEmptyNewsListener);

    }

    public void deregisterModelListener() {
        mModel.removeListener(NewsModel.ChangeEvent.RECEIVED_NEWS, mReceivedNewsListener);
        mModel.removeListener(NewsModel.ChangeEvent.RECEIVED_EMPTY_NEWS, mReceivedEmptyNewsListener);
    }

    private void initializeAdapter() {

        final ArrayList<IHeaderItem> sectionList = new ArrayList<>();

        final DefaultHeaderItem headerItem = new DefaultHeaderItem("", false);
        headerItem.addItem(new WeatherRowItem());

        for (final NewsItemVo itemVo : mModel.getNewsItems()) {
            headerItem.addItem(new NewsRowItem(itemVo));
        }

        sectionList.add(headerItem);

        mAdapter = new StickyHeaderAdapter(mContext, sectionList);
        mAdapter.setOnItemClickListener(mListItemClickListener);
        mNewsList.setAdapter(mAdapter);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNewsList        = (HeaderListView) findViewById(R.id.newsListView);
        mNewsProgressBar = (ProgressBar)    findViewById(R.id.newsProgressIndicator);

        // To prevent crashing.
        // See: https://github.com/applidium/HeaderListView/issues/28
        //MS neue Lösung, da ab 2021 die Ressourcen nun in separaten Namensräumen verwendet werden
        //mNewsList.getListView().setId(R.id.listMode);
        final HeaderListView yourListView = (HeaderListView) findViewWithTag("HeaderListViewTag");
        mNewsList.getListView().setId( yourListView.getId() );

    }

    private final EventListener mReceivedNewsListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            mNewsProgressBar.setVisibility(GONE);
            initializeAdapter();
        }
    };

    private final EventListener mReceivedEmptyNewsListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {

        }
    };

    private final AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, int position, final long id) {
            if (mViewListener != null) {
                // As the WeatherHeader is interpreted as a Row item we need to decrement the position
                // to get the correct position in the news list and check if the position is not
                // 0 (the WeatherHeader)
                if (position > 0) {
                    mViewListener.onNewsItemClick(--position);
                }
            }
        }
    };

    private final Context             mContext;

    private final NewsModel           mModel;
    private StickyHeaderAdapter mAdapter;

    private ProgressBar         mNewsProgressBar;

    private HeaderListView      mNewsList;

    private ViewListener        mViewListener;

}
