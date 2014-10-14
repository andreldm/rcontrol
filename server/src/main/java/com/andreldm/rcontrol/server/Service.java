package com.andreldm.rcontrol.server;

import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;
import org.teleal.cling.binding.annotations.UpnpStateVariable;

@UpnpService(
		serviceId = @UpnpServiceId("RControl"),
        serviceType = @UpnpServiceType(value = "RControl", version = 1)
)
public class Service {
    @UpnpStateVariable(defaultValue="0", sendEvents = false)
    private Integer command;

    @UpnpAction
    public void sendCommand(@UpnpInputArgument(name = "Command") Integer command) {
        System.out.println("Command is: " + command);
        
        String description = Constants.commandsDescription.get(command);

        if(description != null) {
        	MessageDispatcher.getInstance().dispatchMessage("Command received: " + description);
        }
        
        try {
        	SendKey.send(command);
        }catch(Throwable e) {
        	e.printStackTrace();
        }
    }
}
