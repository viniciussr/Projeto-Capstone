package com.capstone.runapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.capstone.runapp.R;
import com.capstone.runapp.model.Event;
import com.capstone.runapp.util.Format;

/**
 * Created by vinicius.rocha on 3/15/18.
 */

public class WidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Event event) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_info);
        views.setTextViewText(R.id.w_name, event.nome());
        views.setTextViewText(R.id.w_data, Format.dateFormat(event.data()));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
