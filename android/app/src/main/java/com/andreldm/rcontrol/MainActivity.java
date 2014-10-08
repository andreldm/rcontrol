package com.andreldm.rcontrol;

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


public class MainActivity extends ListActivity {
    private AndroidUpnpService upnpService;

    private ArrayAdapter<DeviceDisplay> listAdapter;

    private BrowseRegistryListener registryListener = new BrowseRegistryListener();

    public ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            // Refresh the list with all known devices
            listAdapter.clear();
            for (Device device : upnpService.getRegistry().getDevices()) {
                registryListener.deviceAdded(device);
            }

            // Getting ready for future device advertisements
            upnpService.getRegistry().addListener(registryListener);

            // Search asynchronously for all devices
            upnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    public class BrowseRegistryListener extends DefaultRegistryListener {
        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(
                            MainActivity.this, "Discovery failed of '" + device.getDisplayString() + "': " +
                                (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
            deviceRemoved(device);
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            deviceRemoved(device);
        }

        public void deviceAdded(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Service service = device.findService(Constants.SERVICE_ID);
                    if(service == null) {
                        return;
                    }

                    DeviceDisplay d = new DeviceDisplay(device);
                    int position = listAdapter.getPosition(d);
                    if (position >= 0) {
                        // Device already in the list, re-set new value at same position
                        listAdapter.remove(d);
                        listAdapter.insert(d, position);
                    } else {
                        listAdapter.add(d);
                    }
                }
            });
        }

        public void deviceRemoved(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    listAdapter.remove(new DeviceDisplay(device));
                }
            });
        }
    }

    private void dispatchCommand(int command) {
        for(int i = 0; i < listAdapter.getCount(); i++) {
            DeviceDisplay dd = listAdapter.getItem(i);
            Device d = dd.getDevice();

            Service s = d.findService(Constants.SERVICE_ID);
            if(s == null) continue;

            executeAction(s, command);
        }
    }

    private void executeAction(Service service, int command) {
        if(service == null || upnpService == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        ActionInvocation action = new ActionInvocation(service.getAction("SendCommand"));
        action.setInput("Command", command);

        upnpService.getControlPoint().execute(new ActionCallback(action) {
            @Override
            public void success(ActionInvocation invocation) {
                assert invocation.getOutput().length == 0;
                System.out.println("Successfully called action!");
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                System.err.println(defaultMsg);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAdapter = new ArrayAdapter<DeviceDisplay>(this,
            android.R.layout.simple_list_item_1);
        setListAdapter(listAdapter);

        getApplicationContext().bindService(
                new Intent(this, RControlService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
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

        if (id == R.id.action_search_devices && upnpService != null) {
            upnpService.getRegistry().removeAllRemoteDevices();
            upnpService.getControlPoint().search();
        }


        return super.onOptionsItemSelected(item);
    }

    public void onPlayPause(View view) {
        dispatchCommand(Constants.CMD_PLAY);
    }

    public void onNext(View view) {
        dispatchCommand(Constants.CMD_NEXT);
    }

    public void onPrevious(View view) {
        dispatchCommand(Constants.CMD_PREVIOUS);
    }
}
