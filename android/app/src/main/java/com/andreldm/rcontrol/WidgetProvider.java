package com.andreldm.rcontrol;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Observable;
import java.util.Observer;

public class WidgetProvider extends AppWidgetProvider implements Observer {
    private RemoteViews remoteViews = null;
    private Context context = null;
    private int[] appWidgetIds;

	public final static String ACTION_WIDGET_PLAY_PAUSE = "1";
	public final static String ACTION_WIDGET_PREVIOUS = "2";
	public final static String ACTION_WIDGET_NEXT = "3";
	public final static String ACTION_WIDGET_VOL_MINUS = "4";
	public final static String ACTION_WIDGET_VOL_PLUS = "5";
	public final static String ACTION_WIDGET_SYNC = "6";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("onUpdate");
        this.context = context;
        this.appWidgetIds = appWidgetIds;
	    remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

	    attatchAction(context, remoteViews, R.id.button_play_pause, ACTION_WIDGET_PLAY_PAUSE);
	    attatchAction(context, remoteViews, R.id.button_previous, ACTION_WIDGET_PREVIOUS);
	    attatchAction(context, remoteViews, R.id.button_next, ACTION_WIDGET_NEXT);
	    attatchAction(context, remoteViews, R.id.button_vol_minus, ACTION_WIDGET_VOL_MINUS);
	    attatchAction(context, remoteViews, R.id.button_vol_plus, ACTION_WIDGET_VOL_PLUS);
	    attatchAction(context, remoteViews, R.id.button_sync, ACTION_WIDGET_SYNC);

        Communicator.getInstance().addObserver(this);

	    appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

        // FIXME: This is dangerous, the service may be running as
        // long as the widget lives
        context.getApplicationContext().bindService(
                new Intent(context.getApplicationContext(), RControlService.class),
                Communicator.getInstance().getServiceConnection(),
                Context.BIND_AUTO_CREATE
        );

//        // Get all ids
//        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
//        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
//
//        Intent intent = new Intent(context.getApplicationContext(), RControlService.class);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
//        context.startService(intent);
    }

	private void attatchAction(Context context, RemoteViews remoteViews, int viewId, String action) {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
	    remoteViews.setOnClickPendingIntent(viewId, pendingIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        System.out.println("onReceive");
        String action = intent.getAction();
        Communicator comm = Communicator.getInstance();

        if(action.equals(ACTION_WIDGET_PLAY_PAUSE))
            comm.dispatchCommand(Constants.CMD_PLAY);
        else if(action.equals(ACTION_WIDGET_PREVIOUS))
            comm.dispatchCommand(Constants.CMD_PREVIOUS);
        else if(action.equals(ACTION_WIDGET_NEXT))
            comm.dispatchCommand(Constants.CMD_NEXT);
        else if(action.equals(ACTION_WIDGET_VOL_MINUS))
            comm.dispatchCommand(Constants.CMD_VOLDOWN);
        else if(action.equals(ACTION_WIDGET_VOL_PLUS))
            comm.dispatchCommand(Constants.CMD_VOLUP);
        else if(action.equals(ACTION_WIDGET_SYNC))
            comm.refresh();
        else
	        super.onReceive(context, intent);
	}

    @Override
    public void update(Observable observable, Object data) {
        if(context == null || remoteViews == null) {
            return;
        }

        if (data instanceof Integer) {
            final Integer i = (Integer)data;
            System.out.println(i > 0 ? "On" : "Off");
            remoteViews.setImageViewResource(R.id.button_sync, i > 0
                    ? R.drawable.ic_sync_on
                    : R.drawable.ic_sync_off);

            AppWidgetManager.getInstance(context).updateAppWidget(appWidgetIds, remoteViews);
        }
    }
}
