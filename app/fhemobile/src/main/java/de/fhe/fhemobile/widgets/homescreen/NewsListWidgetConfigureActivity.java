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
package de.fhe.fhemobile.widgets.homescreen;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.fhe.fhemobile.BuildConfig;
import de.fhe.fhemobile.R;
import de.fhe.fhemobile.adapters.news.NewsCategoryAdapter;
import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.vos.news.NewsCategoryResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * The configuration screen for the {@link NewsListWidget NewsListWidget} AppWidget.
 */
public class NewsListWidgetConfigureActivity extends Activity {


    private static final String PREFS_NAME = "de.fhe.fhemobile.widgets.homescreen.NewsListWidget" + BuildConfig.FLAVOR;
    private static final String PREF_PREFIX_KEY = "newslist_appwidget_";

    public NewsListWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.news_list_widget_configure);

        mAvailableCategories = (ListView) findViewById(R.id.widgetNewsCategoryList);
        mAvailableCategories.setOnItemClickListener(mItemClickListener);

        // Find the widget id from the intent.
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        NetworkHandler.getInstance().fetchAvailableNewsLists(mNewsCategoryCallback);
    }

    /**
     * This method right now displays the widget and starts a Service to fetch
     * remote data from Server
     */
    void startWidget() {

        // this intent is essential to show the widget
        // if this intent is not included,you can't show
        // widget on homescreen
        final Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(Activity.RESULT_OK, intent);

        // start your service
        // to fetch data from web
        final Intent serviceIntent = new Intent(this, NewsListRemoteFetchService.class);
        serviceIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        startService(serviceIntent);

        // finish this activity
        this.finish();

    }

    private final AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

            // Save News Id to prefs
            saveNewsWidgetPref(
                    NewsListWidgetConfigureActivity.this,
                    mAppWidgetId,
                    String.valueOf(mCategoryAdapter.getItem(position).getId())
            );

            startWidget();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveNewsWidgetPref(final Context context, final int appWidgetId, final String categoryId) {
        final SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, categoryId);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadNewsWidgetPref(final Context context, final int appWidgetId) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY + appWidgetId, "0");
    }

    static void deleteNewsWidgetPref(final Context context, final int appWidgetId) {
        final SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    private final Callback<NewsCategoryResponse> mNewsCategoryCallback = new Callback<NewsCategoryResponse>() {
        @Override
        public void onResponse(final Call<NewsCategoryResponse> call, final Response<NewsCategoryResponse> response) {
            if ((mAvailableCategories != null) && (response.body().getNewsCategories() != null) && (response.body() != null)) {
                mCategoryAdapter = new NewsCategoryAdapter(NewsListWidgetConfigureActivity.this, response.body().getNewsCategories());
                mAvailableCategories.setAdapter(mCategoryAdapter);
            }
        }

        @Override
        public void onFailure(final Call<NewsCategoryResponse> call, final Throwable t) {

        }

    };

    int                 mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    ListView            mAvailableCategories;
    NewsCategoryAdapter mCategoryAdapter;
}

