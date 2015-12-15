package com.andreldm.rcontrol.server;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class Server {
	private SocketIOServer server;

	private void sendCommand(Integer command) {
		System.out.println("Command received: " + command);

		String description = Constants.commandsDescription.get(command);

		if (description != null) {
			MessageDispatcher.getInstance().dispatch("Command received: " + description);
		}

		try {
			SendKey.send(command);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void start(int port) {
		Configuration config = new Configuration();
		config.setHostname("0.0.0.0");
		config.setPort(port);

		if (server != null) {
			server.stop();
		}

		server = new SocketIOServer(config);
		server.addEventListener("rcontrol", String.class, new CommandListener());
		server.start();
	}

	public void stop() {
		server.stop();
	}

	private class CommandListener implements DataListener<String> {
		@Override
		public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
			try {
				Integer command = Integer.valueOf(data);
				sendCommand(command);
			} catch (NumberFormatException e) {
				System.out.println("Unknown command: " + data);
			}
		}
	}
}
