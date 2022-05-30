package com.example.projecttranslator;

import android.appwidget.AppWidgetManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DailyWordWorker extends Worker {

    Context context;

    public DailyWordWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        int appWidgetId;
        if(getTags().toArray()[0].toString().contains("com.example"))   //one tag is the package name
            appWidgetId = Integer.parseInt(getTags().toArray()[1].toString());
        else
            appWidgetId = Integer.parseInt(getTags().toArray()[0].toString());

        Utils.putStringInSP(context, "from_worker","true");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.daily_words_stack_view);

        return Result.success();
    }
}
