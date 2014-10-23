package com.andreldm.rcontrol.server;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;

public class Server implements Runnable {
	static {
		try {
			LogManager.getLogManager().reset();
			Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
			globalLogger.setLevel(java.util.logging.Level.OFF);
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final UpnpService upnpService = new UpnpServiceImpl();

    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                	System.out.println("Shuting down UPnP service...");
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(createDevice());
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public void shutdown() {
    	upnpService.shutdown();
    }

	@SuppressWarnings("unchecked")
	LocalDevice createDevice() throws Exception {
		DeviceIdentity identity =
				new DeviceIdentity(UDN.uniqueSystemIdentifier("RControl"));

		DeviceType type = new UDADeviceType("RControl", 1);

		DeviceDetails details = new DeviceDetails("RControl",
				new ManufacturerDetails("AndreLDM"),
				new ModelDetails("RControl", "A Remote Controller for Android.", "v1"));

		Icon icon = new Icon("image/png", 64, 64, 8,
				getClass().getResource("icon.png"));

		LocalService<Service> service = new AnnotationLocalServiceBinder()
				.read(Service.class);

		service.setManager(new DefaultServiceManager<Service>(
				service, Service.class));

		return new LocalDevice(identity, type, details, icon, service);
	}
}
