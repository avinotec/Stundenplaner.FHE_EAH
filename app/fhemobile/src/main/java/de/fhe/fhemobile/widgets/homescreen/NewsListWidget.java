package de.fhe.fhemobile.widgets.homescreen;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import de.fhe.fhemobile.R;
import de.fhe.fhemobile.activities.news.NewsSingleActivity;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NewsListWidgetConfigureActivity NewsListWidgetConfigureActivity}
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class NewsListWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            fetchData(context, appWidgetId);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            NewsListRemoteFetchService.deleteNewsItems(appWidgetId);
            NewsListWidgetConfigureActivity.deleteNewsWidgetPref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),
                R.layout.news_list_widget
        );

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, NewsListRemoteService.class);

        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(R.id.widgetNewsList, svcIntent);

        remoteViews.setTextViewText(
                R.id.widgetNewsTitle,
                NewsListRemoteFetchService.getNewsChannelName(appWidgetId)
        );

        //setting an empty view in case of no data
        remoteViews.setEmptyView(R.id.widgetNewsList, R.id.widgetNewsListEmpty);

        // Set template for specific list item click. Starts the NewsSingleActivity with the clicked
        // new item.
        Intent intent = new Intent(context, NewsSingleActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.widgetNewsList, pendingIntent);

        // When no data is shown, then allow click on empty view to reload view
        // Sends broadcast for specific appwidget to update itself
        Intent updateIntent = new Intent(context, NewsListWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widgetNewsListEmpty, updatePendingIntent);

        return remoteViews;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            if (intent.getAction().equals(DATA_FETCHED)) {

                AppWidgetManager appWidgetManager = AppWidgetManager
                        .getInstance(context);

                RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

                fetchData(context, appWidgetId);
            }
        }
    }

    private void fetchData(Context _Context, int _AppWidgetId) {
        Intent serviceIntent = new Intent(_Context, NewsListRemoteFetchService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, _AppWidgetId);
        _Context.startService(serviceIntent);
    }

    private static final String LOG_TAG = NewsListWidget.class.getSimpleName();

    public static final String DATA_FETCHED = "de.fhe.fhemobile.DATA_FETCHED";


}

