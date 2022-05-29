package com.example.projecttranslator;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WidgetItemFactory implements RemoteViewsService.RemoteViewsFactory {   //similar to an adopter for widget
    private Context context;
    private int widgetId;
    private ArrayList<String> data;
    private String email;

    public  WidgetItemFactory(Context context, Intent intent){
        this.context = context;
        data = new ArrayList<>();
        //get widget id from intent
        this.widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        email = Utils.getStringFromSP(context, "user_email");
        if(!email.equals("")){
            FirebaseDBManager.readRandomWords(context, 3,email, data, widgetId);
        }
        //set worker
        PeriodicWorkRequest updateWordsWorker = new PeriodicWorkRequest.Builder(DailyWordWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setConstraints(Constraints.NONE)
                .addTag(widgetId+"")
                .build();
        WorkManager.getInstance(context).enqueue(updateWordsWorker);
    }

    @Override
    public void onCreate() {
        //connect to data source

    }

    @Override
    public void onDataSetChanged() {
        if (Utils.getStringFromSP(context, "from_worker").equals("true")){
            data.clear();
            FirebaseDBManager.readRandomWords(context, 3,email, data, widgetId);
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
        views.setTextViewText(R.id.widget_item_text, data.get(i));
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
