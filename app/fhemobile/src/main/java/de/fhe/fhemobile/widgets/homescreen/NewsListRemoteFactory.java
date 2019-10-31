/*
 * Copyright (c) 2014-2019 Fachhochschule Erfurt, Ernst-Abbe-Hochschule Jena
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
package de.fhe.fhemobile.widgets.homescreen;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.news.NewsSingleActivity;
import de.fhe.fhemobile.vos.news.NewsItemVo;

/**
 * Created by Paul Cech on 08.04.15.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class NewsListRemoteFactory implements RemoteViewsService.RemoteViewsFactory {

    public NewsListRemoteFactory(Context _context, Intent _intent) {
        mContext  = _context;
        mWidgetId = _intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        mItems    = NewsListRemoteFetchService.getNewsItems(mWidgetId).clone();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.item_news_list);

        NewsItemVo listItem = mItems[position];

        Intent intent = new Intent();
        intent.putExtra(NewsSingleActivity.EXTRA_NEWS_ITEM, listItem);

        remoteView.setOnClickFillInIntent(R.id.newsItemLayout, intent);
        remoteView.setTextViewText(R.id.newsTitle,       listItem.getTitle());
        remoteView.setTextViewText(R.id.newsDescription, listItem.getDescription());
        remoteView.setTextViewText(R.id.newsPubDate,     listItem.getPubDate());

        if (position < getCount()-1) {
            remoteView.setViewVisibility(R.id.listDivider, View.VISIBLE);
        }

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {

        return new RemoteViews(mContext.getPackageName(), R.layout.part_loading);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static final String LOG_TAG = NewsListRemoteFactory.class.getSimpleName();

    private final NewsItemVo[] mItems;
    private final int          mWidgetId;
    private final Context      mContext;
}
