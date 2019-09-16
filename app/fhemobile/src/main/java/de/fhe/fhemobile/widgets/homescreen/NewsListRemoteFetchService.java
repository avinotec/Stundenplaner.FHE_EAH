package de.fhe.fhemobile.widgets.homescreen;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.SparseArray;

import de.fhe.fhemobile.network.NetworkHandler;
import de.fhe.fhemobile.vos.news.NewsItemResponse;
import de.fhe.fhemobile.vos.news.NewsItemVo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsListRemoteFetchService extends Service {

    public NewsListRemoteFetchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Retrieve appwidget id from intent it is needed to update widget later
    */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            final int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            NetworkHandler.getInstance().fetchNewsData(
                    NewsListWidgetConfigureActivity.loadNewsWidgetPref(getBaseContext(), appWidgetId),
                    new Callback<NewsItemResponse>() {
                        @Override
                        public void onResponse(Call<NewsItemResponse> call, Response<NewsItemResponse> response) {

                            // MS: Bei den News sind die news/0 kaputt
                            if ( response.body() != null ) {
                                // Set data
                                mNewsData.append(appWidgetId, response.body().getChannel().getNewsItems());
                                mNewsChannelNames.append(appWidgetId, response.body().getChannel().getTitle());
                                populateWidget(appWidgetId);
                            }
                        }

                        @Override
                        public void onFailure(Call<NewsItemResponse> call, Throwable t) {

                        }

                    });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public static NewsItemVo[] getNewsItems(int _AppWidgetId) {
        NewsItemVo[] result;

        if (mNewsData == null) {
            mNewsData = new SparseArray<>();
        }

        result = mNewsData.get(_AppWidgetId);

        if (result == null) {
            result = new NewsItemVo[]{};
        }

        return result;
    }

    public static String getNewsChannelName(int _AppWidgetId) {
        if (mNewsChannelNames == null) {
            mNewsChannelNames = new SparseArray<>();
        }
        return mNewsChannelNames.get(_AppWidgetId);
    }

    public static void deleteNewsItems(int _AppWidgetId) {
        if (mNewsData.get(_AppWidgetId, null) != null) {
            mNewsData.delete(_AppWidgetId);
            mNewsChannelNames.delete(_AppWidgetId);
        }
    }

    /**
     * Method which sends broadcast to WidgetProvider
     * so that widget is notified to do necessary action
     * and here action == WidgetProvider.DATA_FETCHED
     */
    private void populateWidget(int _AppWidgetId) {

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(NewsListWidget.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _AppWidgetId);
        sendBroadcast(widgetUpdateIntent);

        stopSelf();
    }

    private static final String LOG_TAG = NewsListRemoteFetchService.class.getSimpleName();

    private static SparseArray<NewsItemVo[]> mNewsData         = new SparseArray<>();
    private static SparseArray<String>       mNewsChannelNames = new SparseArray<>();

}
