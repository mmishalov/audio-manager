package com.easysoft.auxmanager.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import com.easysoft.auxmanager.R;
import com.easysoft.auxmanager.service.AUXManagerService;
import com.easysoft.auxmanager.shared.ActiveProfileActivitiesExecutor;

/**
 * Widget provider
 * <p/>
 * <br/><i>Created at 10/30/14 11:48 PM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.x, JDK 1.7
 */
public class AUXManagerWidgetProvider  extends AppWidgetProvider {
    public static String TOGGLE_AUX_MANAGER = "com.easysoft.auxmanager.widget.TOGGLE_AUX_MANAGER";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            if(AUXManagerService.isServiceActive()) {
                remoteViews.setImageViewResource(R.id.widgetImageButton, R.drawable.widget_on);
            } else {
                remoteViews.setImageViewResource(R.id.widgetImageButton, R.drawable.widget_off);
            }
            Intent intentClick = new Intent(context,AUXManagerWidgetProvider.class);
            intentClick.setAction(TOGGLE_AUX_MANAGER);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId ,intentClick, 0);
            remoteViews.setOnClickPendingIntent(R.id.widgetImageButton, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null && intent.getAction().equals(TOGGLE_AUX_MANAGER)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            // Create a fresh intent
            Intent serviceIntent = new Intent(context, AUXManagerService.class);
            if(AUXManagerService.isServiceActive()) {
                context.stopService(serviceIntent);
                remoteViews.setImageViewResource(R.id.widgetImageButton, R.drawable.widget_off);
            } else {
                context.startService(serviceIntent);
                remoteViews.setImageViewResource(R.id.widgetImageButton, R.drawable.widget_on);
            }
            ComponentName componentName = new ComponentName(context, AUXManagerWidgetProvider.class);
            AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
        }
        super.onReceive(context, intent);
    }
}
