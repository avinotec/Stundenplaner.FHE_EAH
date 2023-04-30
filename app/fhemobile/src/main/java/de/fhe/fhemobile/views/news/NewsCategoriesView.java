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
import android.widget.FrameLayout;
import android.widget.ListView;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.news.NewsCategoryAdapter;
import de.fhe.fhemobile.events.Event;
import de.fhe.fhemobile.events.EventListener;
import de.fhe.fhemobile.models.news.NewsModel;

/**
 * Created by paul on 12.02.14.
 */
public class NewsCategoriesView extends FrameLayout {

    @FunctionalInterface
    public interface ViewListener {
        void onNewsCategoryChosen(Integer _Id, Integer _Position);
    }

    public NewsCategoriesView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mModel = NewsModel.getInstance();

        mModel.addListener(NewsModel.NewsChangeEvent.RECEIVED_CATEGORY_ITEMS, mCategoryItemsListener);
    }

    public void initializeView(final ViewListener _Listener) {
        mViewListener = _Listener;
    }

// --Commented out by Inspection START (23.04.2023 15:11):
//    public void destroy() {
//        mModel.removeListener(NewsModel.NewsChangeEvent.RECEIVED_CATEGORY_ITEMS, mCategoryItemsListener);
//
//        mViewListener = null;
//        mCategoryItemsListener = null;
//    }
// --Commented out by Inspection STOP (23.04.2023 15:11)

    public void initContent() {
        mAdapter = new NewsCategoryAdapter(mContext, mModel.getCategoryItems());
        mCategoryListView.setAdapter(mAdapter);
        mCategoryListView.setOnItemClickListener(mCategorySelectListener);
        mCategoryListView.setItemChecked(mModel.getChosenNewsItemPosition(), true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCategoryListView = (ListView) findViewById(R.id.newsCategoryListView);
    }

    private final AdapterView.OnItemClickListener mCategorySelectListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            mViewListener.onNewsCategoryChosen((int) mAdapter.getItemId(position), position);
        }
    };

    private final EventListener mCategoryItemsListener = new EventListener() {
        @Override
        public void onEvent(final Event event) {
            initContent();
        }
    };

    private final Context             mContext;

    private final NewsModel           mModel;

    ViewListener        mViewListener;
    NewsCategoryAdapter mAdapter;

    private ListView            mCategoryListView;
}
