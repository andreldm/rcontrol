package com.andreldm.rcontrol.mdns;

import java.util.List;

public interface ServiceFinderCallback {
    public void receiveServers(List<ServerInfo> servers);
}
