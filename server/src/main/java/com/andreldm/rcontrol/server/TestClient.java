package com.andreldm.rcontrol.server;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

public class TestClient implements Runnable {

	static {
		try {
			LogManager.getLogManager().reset();
			Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
			globalLogger.setLevel(java.util.logging.Level.OFF);
		}  catch (Exception e) {
			// ignore
		}
	}

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread clientThread = new Thread(new TestClient());
        clientThread.setDaemon(false);
        clientThread.start();
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void executeAction(UpnpService upnpService, Service service) {
		ActionInvocation action = new ActionInvocation(service.getAction("SendCommand"));
		action.setInput("Command", Constants.CMD_PLAY);

		// Executes asynchronous in the background
		upnpService.getControlPoint().execute(
			new ActionCallback(action) {

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

	@SuppressWarnings("rawtypes")
	RegistryListener createRegistryListener(final UpnpService upnpService) {
		return new DefaultRegistryListener() {
			ServiceId serviceId = new UDAServiceId("RControl");

			@Override
			public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
				Service service = device.findService(serviceId);
				if (service != null) {
					System.out.println("Service discovered: " + service);
					executeAction(upnpService, service);
				}
			}

			@Override
			public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
				Service service = device.findService(serviceId);
				if (service != null) {
					System.out.println("Service disappeared: " + service);
				}
			}
		};
	}

    public void run() {
        try {
            UpnpService upnpService = new UpnpServiceImpl();

            // Add a listener for device registration events
            upnpService.getRegistry().addListener(createRegistryListener(upnpService));

            // Broadcast a search message for all devices
            upnpService.getControlPoint().search(new STAllHeader());
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            System.exit(1);
        }
    }

}