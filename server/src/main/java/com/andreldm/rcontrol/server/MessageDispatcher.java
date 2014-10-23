package com.andreldm.rcontrol.server;

import java.util.HashSet;

public class MessageDispatcher {
	private static MessageDispatcher instance = null;
	private HashSet<IMessageReceiver> receivers;

	private MessageDispatcher() {
		receivers = new HashSet<IMessageReceiver>();
	}

	public static MessageDispatcher getInstance(){
		if(instance == null) {
			instance = new MessageDispatcher();
		}

		return instance;
	}

	public void registerReceiver(IMessageReceiver receiver) {
		receivers.add(receiver);
	}

	public void unregisterReceiver(IMessageReceiver receiver) {
		receivers.remove(receiver);
	}

	public void dispatch(String msg) {
		for (IMessageReceiver receiver : receivers) {
			receiver.receiveMessage(msg);
		}
	}
}
