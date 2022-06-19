package com.example.projecttranslator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class DailyWordWidget extends AppWidgetProvider {
public static final String ACTION_CHANGE_TEXT = "com.example.projecttranslator.actioncChangeText";
public static final String EXTRA_ITEM_POSITION = "itemPosition";
public static final String EXTRA_TRANSLATIONS = "extraTranslations";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);   //inserting the id
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));    //distinguish between the widgets

            Intent clickIntent = new Intent(context, DailyWordWidget.class);
            clickIntent.setAction(ACTION_CHANGE_TEXT);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context,0,clickIntent,0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.daily_word_widget);
            views.setRemoteAdapter(R.id.daily_words_stack_view, serviceIntent);
            views.setEmptyView(R.id.daily_words_stack_view, R.id.widget_empty_view);    //when empty, display the text in empty_view
            views.setPendingIntentTemplate(R.id.daily_words_stack_view, clickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.daily_words_stack_view);  //trigger onDataSetChanged in WidgetItemFactory
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_CHANGE_TEXT)){
            ArrayList<String> translations = intent.getStringArrayListExtra(EXTRA_TRANSLATIONS);        //return all the translations of all of the cards
            int clickedPosition = intent.getIntExtra(EXTRA_ITEM_POSITION, 0);
            Toast.makeText(context, translations.get(clickedPosition), Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
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