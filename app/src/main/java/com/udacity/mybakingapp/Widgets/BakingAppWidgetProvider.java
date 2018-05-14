package com.udacity.mybakingapp.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.udacity.mybakingapp.Defines;
import com.udacity.mybakingapp.MainActivity;
import com.udacity.mybakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_recipe);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        SharedPreferences prefs= context.getSharedPreferences(Defines.BAKINGAPP_WIDGET,Context.MODE_PRIVATE);
        int recipeId;
        if (prefs.contains(Defines.RECIPE_ID))
            recipeId=prefs.getInt(Defines.RECIPE_ID,-1);
        String ingredients="";
        if (prefs.contains(Defines.INGREDIENTS))
            ingredients=prefs.getString(Defines.INGREDIENTS,"");
        if (!ingredients.isEmpty())
            views.setTextViewText(R.id.appwidget_text, ingredients);

        Intent intent= new Intent(context, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
        views.setOnClickPendingIntent(appWidgetId,pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(R.id.widget_area, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        SharedPreferences prefs= context.getSharedPreferences(Defines.BAKINGAPP_WIDGET,Context.MODE_PRIVATE);
        int recipeId=prefs.getInt(Defines.RECIPE_ID,-1);
        String ingredients=prefs.getString(Defines.INGREDIENTS,"");

    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

