package com.andreldm.rcontrol;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import java.util.Observable;
import java.util.Observer;


public class MainActivity extends Activity implements Observer {
    private Communicator communicator;
    private TextView deviceCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        communicator = Communicator.getInstance();
        communicator.addObserver(this);

        deviceCount = (TextView) findViewById(R.id.deviceCount);

        getApplicationContext().bindService(
                new Intent(this, RControlService.class),
                communicator.getServiceConnection(),
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        communicator.deleteObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        communicator.deleteObserver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search_devices) {
            communicator.refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPlayPause(View view) {
        communicator.dispatchCommand(Constants.CMD_PLAY);
    }

    public void onNext(View view) {
        communicator.dispatchCommand(Constants.CMD_NEXT);
    }

    public void onPrevious(View view) {
        communicator.dispatchCommand(Constants.CMD_PREVIOUS);
    }

    public void onVolUp(View view) {
        communicator.dispatchCommand(Constants.CMD_VOLUP);
    }

    public void onVolDown(View view) {
        communicator.dispatchCommand(Constants.CMD_VOLDOWN);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Integer) {
            final Integer i = (Integer)data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceCount.setText(i.toString());
                }
            });
        }
    }
}
