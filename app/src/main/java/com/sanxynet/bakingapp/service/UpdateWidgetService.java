package com.sanxynet.bakingapp.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.sanxynet.bakingapp.widget.RecipeAppWidget;
import timber.log.Timber;

import static com.sanxynet.bakingapp.utils.Costants.RECIPE_WIDGET_UPDATE;

public class UpdateWidgetService extends IntentService {
    public UpdateWidgetService() {
        super("UpdateWidgetService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        widgetUpdate(getApplicationContext());
    }

    public static void startWidgetService(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }

    private static void widgetUpdate(Context context) {
        try {
            Intent intent = new Intent(context, RecipeAppWidget.class);
            intent.setAction(RECIPE_WIDGET_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Timber.e("pending%s", e.getMessage());
        }
    }

}
