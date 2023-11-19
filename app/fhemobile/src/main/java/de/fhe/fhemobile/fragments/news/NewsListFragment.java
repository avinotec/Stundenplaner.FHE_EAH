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

package de.fhe.fhemobile.fragments.news;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.SettingsActivity;
import de.fhe.fhemobile.activities.news.NewsSingleActivity;
import de.fhe.fhemobile.fragments.FeatureFragment;
import de.fhe.fhemobile.models.news.NewsModel;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.utils.feature.Features;
import de.fhe.fhemobile.views.news.NewsListView;

public class NewsListFragment extends FeatureFragment {

    public static final String TAG = NewsListFragment.class.getSimpleName();

    public NewsListFragment() {
        super(TAG);
    }

    public static NewsListFragment newInstance() {
        final NewsListFragment fragment = new NewsListFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(NewsModel.getInstance().getNewsItems() == null) {
            NetworkHandler.getInstance().fetchNewsData();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (NewsListView) inflater.inflate(R.layout.fragment_news_list, container, false);
        mView.initializeView(mViewListener);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //replacement of deprecated setHasOptionsMenu(), onCreateOptionsMenu() and onOptionsItemSelected()
        // see https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01
        final MenuHost menuHost = requireActivity();
        final Activity activity = getActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater) {

                menu.clear();
                // Add menu items here
                menuInflater.inflate(R.menu.menu_newslist, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull final MenuItem menuItem) {
                // Handle the menu selection
                if (menuItem.getItemId() == R.id.action_settings_news) {
                    final Intent intent = new Intent(activity, SettingsActivity.class);
                    intent.putExtra(SettingsActivity.EXTRA_SETTINGS_ID, Features.FeatureId.NEWS);
                    activity.startActivity(intent);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mView.registerModelListener();
    }

    @Override
    public void onPause() {
        super.onPause();

        mView.deregisterModelListener();
    }


    private final NewsListView.ViewListener mViewListener = new NewsListView.ViewListener() {
        @Override
        public void onNewsItemClick(final Integer _Id) {
            final Intent intent = new Intent(getActivity(), NewsSingleActivity.class);
            intent.putExtra(NewsSingleActivity.EXTRA_NEWS_ITEM, NewsModel.getInstance().getNewsItems()[_Id]);
            startActivity(intent);
        }
    };

    private NewsListView mView;

}
