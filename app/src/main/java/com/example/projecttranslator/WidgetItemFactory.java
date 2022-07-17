package com.example.projecttranslator;

import static com.example.projecttranslator.DailyWordWidget.EXTRA_ITEM_POSITION;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.common.api.Response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WidgetItemFactory implements RemoteViewsService.RemoteViewsFactory {   //similar to an adopter for widget
    private Context context;
    private int widgetId;
    private ArrayList<String> data;
    private ArrayList<String> dataTranslations;
    private String email;

    public WidgetItemFactory(Context context, Intent intent){
        this.context = context;
        data = new ArrayList<>();
        dataTranslations = new ArrayList<>();
        //get widget id from intent
        this.widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        email = Utils.getStringFromSP(context, "user_email");
        if(!email.equals(""))
            FirebaseDBManager.readRandomWords(context, Integer.parseInt(Utils.getStringFromSP(context,"number_of_cards")),email, data,dataTranslations, widgetId);
        //set worker
        PeriodicWorkRequest updateWordsWorker = new PeriodicWorkRequest.Builder(DailyWordWorker.class, 24, TimeUnit.HOURS)
                .setConstraints(Constraints.NONE)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED).build())
                .addTag(widgetId+"")
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("update widget", ExistingPeriodicWorkPolicy.REPLACE, updateWordsWorker);
    }

    @Override
    public void onCreate() {
        //connect to data source
    }

    @Override
    public void onDataSetChanged() {
        email = Utils.getStringFromSP(context, "user_email");
        if (Utils.getStringFromSP(context, "from_worker").equals("true") && !email.equals("")){
            data.clear();
            dataTranslations.clear();
            FirebaseDBManager.readRandomWords(context, Integer.parseInt(Utils.getStringFromSP(context,"number_of_cards")),email, data, dataTranslations, widgetId);
        }
        Utils.putStringInSP(context, "from_worker","false");
    }

    @Override
    public void onDestroy() {
        //close data source
        WorkManager.getInstance(context).cancelAllWorkByTag(widgetId+"");
        data = null;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        if(data.size() > i)
            views.setTextViewText(R.id.widget_item_text, data.get(i));
        //set on click on each card
        Intent fillIntent = new Intent();
        fillIntent.putStringArrayListExtra(DailyWordWidget.EXTRA_TRANSLATIONS, dataTranslations);
        fillIntent.putStringArrayListExtra(DailyWordWidget.EXTRA_WORDS, data);
        fillIntent.putExtra(DailyWordWidget.EXTRA_ITEM_POSITION, i);
        views.setOnClickFillInIntent(R.id.widget_item_text, fillIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;       //type of different items
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
