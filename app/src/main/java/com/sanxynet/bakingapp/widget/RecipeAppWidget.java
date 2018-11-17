package com.sanxynet.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Objects;

import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.ui.MainActivity;
import com.sanxynet.bakingapp.service.RecipeWidgetService;
import com.sanxynet.bakingapp.utils.Costants;
import com.sanxynet.bakingapp.utils.PrefManager;

import static com.sanxynet.bakingapp.utils.Costants.RECIPE_WIDGET_UPDATE;

public class RecipeAppWidget extends AppWidgetProvider {

    private RemoteViews viewsUpdateRecipeWidget(Context context) {

        String widgetRecipeName = PrefManager.getStringPref(context, R.string.pref_widget_name);

        if(TextUtils.isEmpty(widgetRecipeName)) widgetRecipeName = context.getString(R.string.app_name);

        int id = PrefManager.getIntPref(context, R.string.pref_widget_id);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);

        if (id > 0) {

            views.setViewVisibility(R.id.widget_text_error, View.GONE);
            views.setViewVisibility(R.id.widget_listView, View.VISIBLE);

            if (!TextUtils.isEmpty(widgetRecipeName)) {
                views.setTextViewText(R.id.recipe_widget_name, widgetRecipeName);

            }


            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(Costants.EXTRA_RECIPE_WIDGET_ID, id);
            intent.putExtra(Costants.EXTRA_RECIPE_NAME, widgetRecipeName);
            PendingIntent pendingIntent = PendingIntent.getActivities(context, id, new Intent[]{intent}, 0);
            views.setOnClickPendingIntent(R.id.recipe_widget_name, pendingIntent);

            Intent serviceIntent = new Intent(context, RecipeWidgetService.class);
            views.setRemoteAdapter(R.id.widget_listView, serviceIntent);

        } else {
            views.setViewVisibility(R.id.widget_listView, View.GONE);
            views.setViewVisibility(R.id.widget_text_error, View.VISIBLE);
            views.setTextViewText(R.id.widget_text_error, context.getString(R.string.widget_text_error));

            if (TextUtils.isEmpty(widgetRecipeName)) {
                views.setTextViewText(R.id.recipe_widget_name, widgetRecipeName);

            }

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[]{intent}, 0);
            views.setOnClickPendingIntent(R.id.widget_text_error,pendingIntent);
        }
        return views;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeAppWidget.class));

        String action = intent.getAction();
        if (Objects.equals(action, RECIPE_WIDGET_UPDATE)) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listView);
            for(int appWidgetId:appWidgetIds){
               appWidgetManager.partiallyUpdateAppWidget(appWidgetId, viewsUpdateRecipeWidget(context));
           }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        appWidgetManager.updateAppWidget(appWidgetIds, viewsUpdateRecipeWidget(context));
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }


}

