package com.andreldm.rcontrol.server;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class Service {
	private static String SERVICE_TYPE = "_rcontrol._tcp.local.";
	private static String SERVICE_NAME = "rcontrol";
	private static String SERVICE_DESCRIPTION = "A Remote Controller for Android";

	private JmDNS jmdns;

	public void start(int port) {
		try {
			InetAddress inetAddress = Util.getInetAddress();
			// TODO check null
			
			jmdns = JmDNS.create(inetAddress);
			jmdns.registerService(ServiceInfo.create(SERVICE_TYPE, SERVICE_NAME, port, SERVICE_DESCRIPTION));
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Handle, at least log
		}
	}

	public void stop() {
		try {
			jmdns.unregisterAllServices();
			jmdns.close();
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Handle, at least log
		}
	}
}
