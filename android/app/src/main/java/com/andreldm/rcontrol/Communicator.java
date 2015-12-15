package com.andreldm.rcontrol;

import android.util.Log;

import com.andreldm.rcontrol.mdns.ServerInfo;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Communicator {
    private ServerInfo mService;
    private Socket mSocket;

    private Communicator() { }

    // Thread-safe singleton requires this trick
    private static class CommunicatorHolder {
        public static final Communicator INSTANCE = new Communicator();
    }

    public static Communicator getInstance() {
        return CommunicatorHolder.INSTANCE;
    }

    public void setServer(ServerInfo service) {
        mService = service;
        disconnect();
        connect();
    }

    public boolean isConnected() {
        return mSocket != null && mSocket.connected();
    }

    public void connect() {
        try {
            mSocket = IO.socket(mService.getURI());
            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            mSocket.disconnect();
        }
    }

    public void send(int command) throws ServerDisconnectedException {
        Log.d(Constants.TAG, "onSend");

        if (!isConnected()) {
            throw new ServerDisconnectedException();
        }

        mSocket.emit("rcontrol", command);
    }
}
