package com.andreldm.rcontrol;


public class Communicator {
	private static final Communicator instance = new Communicator();
	private static String SERVER_URL = "http://192.168.1.9:3000/";

	private Communicator() {
	}

	public static Communicator getInstance() {
		return instance;
	}

	public void sendPlayPause() {
		new RequestTask().execute(SERVER_URL + "play_pause");
	}

	public void sendBackward() {
		new RequestTask().execute(SERVER_URL + "backward");
	}

	public void sendForward() {
		new RequestTask().execute(SERVER_URL + "forward");
	}

	public void sendVolLess() {
		new RequestTask().execute(SERVER_URL + "vol_less");
	}

	public void sendVolMore() {
		new RequestTask().execute(SERVER_URL + "vol_more");
	}
}
