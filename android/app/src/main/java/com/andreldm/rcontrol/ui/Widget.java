package com.andreldm.rcontrol.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.andreldm.rcontrol.Communicator;
import com.andreldm.rcontrol.Constants;
import com.andreldm.rcontrol.R;
import com.andreldm.rcontrol.ServerDisconnectedException;

public class Widget extends AppWidgetProvider {
    private RemoteViews remoteViews = null;

	public final static String ACTION_WIDGET_PLAY_PAUSE = "1";
	public final static String ACTION_WIDGET_PREVIOUS = "2";
	public final static String ACTION_WIDGET_NEXT = "3";
	public final static String ACTION_WIDGET_VOL_MINUS = "4";
	public final static String ACTION_WIDGET_VOL_PLUS = "5";
	public final static String ACTION_WIDGET_SYNC = "6";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("onUpdate");
	    remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

	    attachAction(context, remoteViews, R.id.button_play_pause, ACTION_WIDGET_PLAY_PAUSE);
	    attachAction(context, remoteViews, R.id.button_previous, ACTION_WIDGET_PREVIOUS);
	    attachAction(context, remoteViews, R.id.button_next, ACTION_WIDGET_NEXT);
	    attachAction(context, remoteViews, R.id.button_vol_minus, ACTION_WIDGET_VOL_MINUS);
	    attachAction(context, remoteViews, R.id.button_vol_plus, ACTION_WIDGET_VOL_PLUS);
	    attachAction(context, remoteViews, R.id.button_sync, ACTION_WIDGET_SYNC);

	    appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

	private void attachAction(Context context, RemoteViews remoteViews, int viewId, String action) {
        Intent intent = new Intent(context, Widget.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
	    remoteViews.setOnClickPendingIntent(viewId, pendingIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        System.out.println("onReceive");
        String action = intent.getAction();
        Communicator comm = Communicator.getInstance();

        try {
        if(action.equals(ACTION_WIDGET_PLAY_PAUSE))
            comm.send(Constants.CMD_PLAY);
        else if(action.equals(ACTION_WIDGET_PREVIOUS))
            comm.send(Constants.CMD_PREVIOUS);
        else if(action.equals(ACTION_WIDGET_NEXT))
            comm.send(Constants.CMD_NEXT);
        else if(action.equals(ACTION_WIDGET_VOL_MINUS))
            comm.send(Constants.CMD_VOLDOWN);
        else if(action.equals(ACTION_WIDGET_VOL_PLUS))
            comm.send(Constants.CMD_VOLUP);
        else if(action.equals(ACTION_WIDGET_SYNC))
            throw new ServerDisconnectedException();
        else
	        super.onReceive(context, intent);
        } catch (ServerDisconnectedException e) {
            Intent i = new Intent(context, PickServerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        }
	}
}
