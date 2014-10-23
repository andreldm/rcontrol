package com.andreldm.rcontrol;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
    private Communicator communicator;

	public final static String ACTION_WIDGET_PLAY_PAUSE = "ActionReceiverPlayPause";
	public final static String ACTION_WIDGET_BACKWARD = "ActionReceiverBackward";
	public final static String ACTION_WIDGET_FORWARD = "ActionReceiverForward";
	public final static String ACTION_WIDGET_VOL_LESS = "ActionReceiverVolLess";
	public final static String ACTION_WIDGET_VOL_MORE = "ActionReceiverVolMore";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

	    attatchAction(context, remoteViews, R.id.button_play_pause, ACTION_WIDGET_PLAY_PAUSE);
	    attatchAction(context, remoteViews, R.id.button_backward, ACTION_WIDGET_BACKWARD);
	    attatchAction(context, remoteViews, R.id.button_forward, ACTION_WIDGET_FORWARD);
	    attatchAction(context, remoteViews, R.id.button_vol_less, ACTION_WIDGET_VOL_LESS);
	    attatchAction(context, remoteViews, R.id.button_vol_more, ACTION_WIDGET_VOL_MORE);

	    appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}

	private void attatchAction(Context context, RemoteViews remoteViews, int viewId, String action) {
		Intent active;
		PendingIntent actionPendingIntent;

	    active = new Intent(context, WidgetProvider.class);
	    active.setAction(action);
	    actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
	    remoteViews.setOnClickPendingIntent(viewId, actionPendingIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

        if(communicator == null) {
            communicator = Communicator.getInstance();
        }

        if(action.equals(ACTION_WIDGET_PLAY_PAUSE))
            communicator.dispatchCommand(Constants.CMD_PLAY);
        else if(action.equals(ACTION_WIDGET_BACKWARD))
            communicator.dispatchCommand(Constants.CMD_PREVIOUS);
        else if(action.equals(ACTION_WIDGET_FORWARD))
            communicator.dispatchCommand(Constants.CMD_NEXT);
        else if(action.equals(ACTION_WIDGET_VOL_LESS))
            communicator.dispatchCommand(Constants.CMD_VOLDOWN);
        else if(action.equals(ACTION_WIDGET_VOL_MORE))
            communicator.dispatchCommand(Constants.CMD_VOLUP);
        else
	        super.onReceive(context, intent);
	}
}
