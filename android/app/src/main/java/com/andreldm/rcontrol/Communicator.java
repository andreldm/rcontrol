package com.andreldm.rcontrol;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Communicator extends Observable {
    private List<Device> listDevice = null;
    private AndroidUpnpService upnpService;
    private CustomRegistryListener registryListener;

    private Communicator() {
        listDevice = new ArrayList<>();
        registryListener = new CustomRegistryListener();
    }

    // Thread-safe singleton requires this trick
    private static class CommunicatorHolder {
        public static final Communicator INSTANCE = new Communicator();
    }

    public static Communicator getInstance() {
        return CommunicatorHolder.INSTANCE;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    @SuppressWarnings("unchecked")
    public void executeAction(Service service, int command) {
        if(service == null || upnpService == null) {
            return;
        }

        ActionInvocation action = new ActionInvocation(service.getAction("SendCommand"));
        action.setInput("Command", command);

        upnpService.getControlPoint().execute(new ActionCallback(action) {
            @Override
            public void success(ActionInvocation i) {
                System.out.println("Successfully called action!");
            }

            @Override
            public void failure(ActionInvocation i, UpnpResponse r, String msg) {
                System.err.println(msg);
            }
        });
    }

    public void refresh() {
        System.out.println(upnpService);
        if (upnpService != null) {
            upnpService.getRegistry().removeAllRemoteDevices();
            upnpService.getControlPoint().search();
        }
    }

    public void dispatchCommand(int command) {
        for(Device d : listDevice) {
            Service s = d.findService(Constants.SERVICE_ID);
            if(s != null) executeAction(s, command);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            // Refresh the list with all known devices
            listDevice.clear();
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

    public class CustomRegistryListener extends DefaultRegistryListener {
        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
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

        public void deviceAdded(Device device) {
            Service service = device.findService(Constants.SERVICE_ID);
            if(service != null) {
                listDevice.add(device);
                setChanged();
                notifyObservers(listDevice.size());
            }
        }

        public void deviceRemoved(Device device) {
            listDevice.remove(device);
            setChanged();
            notifyObservers(listDevice.size());
        }
    }
}
