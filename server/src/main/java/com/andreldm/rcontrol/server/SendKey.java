package com.andreldm.rcontrol.server;

// Util.loadLib() has to be called previously!
public class SendKey {
	private native static void sendkey(int key);

	public static void send(int cmd) {
		Integer key = Constants.commands.get(cmd);

		if(key == null) {
			return;
		}

		sendkey(key.intValue());
	}
}
