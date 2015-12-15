package com.andreldm.rcontrol.mdns;

import java.io.Serializable;

public class ServerInfo implements Serializable {
    private String mAddress;
    private int mPort;

    public ServerInfo() { }

    public ServerInfo(String address, int port) {
        mAddress = address;
        mPort = port;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        mPort = port;
    }

    public String getURI() {
        return "http://" + mAddress + ":" + mPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerInfo that = (ServerInfo) o;

        if (mPort != that.mPort) return false;
        return !(mAddress != null ? !mAddress.equals(that.mAddress) : that.mAddress != null);
    }

    @Override
    public int hashCode() {
        int result = mAddress != null ? mAddress.hashCode() : 0;
        result = 31 * result + mPort;
        return result;
    }

    @Override
    public String toString() {
        return mAddress + ':' + mPort;
    }
}
