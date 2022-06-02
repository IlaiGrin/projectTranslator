package com.example.projecttranslator;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class DailyWordWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);   //inserting the id
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));    //distinguish between the widgets

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.daily_word_widget);
            views.setRemoteAdapter(R.id.daily_words_stack_view, serviceIntent);
            views.setEmptyView(R.id.daily_words_stack_view, R.id.widget_empty_view);    //when empty, display the text in empty_view

            Utils.putStringInSP(context,"from_worker","true");
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.daily_words_stack_view);  //trigger onDataSetChanged in WidgetItemFactory
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
}