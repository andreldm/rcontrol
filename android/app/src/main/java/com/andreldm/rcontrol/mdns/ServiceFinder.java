package com.andreldm.rcontrol.mdns;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.andreldm.rcontrol.Constants;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceFinder {
    private static final String SERVICE_TYPE = "_rcontrol._tcp.";

    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo mServiceInfo;

    private Set<ServerInfo> mServers;

    public ServiceFinder(Context context) {
        mServers = new HashSet<>();
        mNsdManager = (NsdManager)(context.getSystemService(Context.NSD_SERVICE));
        initializeResolveListener();
        initializeDiscoveryListener();
    }

    public void startDiscovery() {
        mServers.clear();
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public List<ServerInfo> getServers() {
        List<ServerInfo> list = new ArrayList<>();
        list.addAll(mServers);
        return list;
    }

    private void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String regType) {
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                String name = service.getServiceName();
                String type = service.getServiceType();
                Log.d(Constants.TAG, "Service Name=" + name);
                Log.d(Constants.TAG, "Service Type=" + type);
                if (type.equals(SERVICE_TYPE) && name.contains("rcontrol")) {
                    Log.d(Constants.TAG, "Service Found @ '" + name + "'");
                    mNsdManager.resolveService(service, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    private void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(Constants.TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                mServiceInfo = serviceInfo;

                int port = mServiceInfo.getPort();
                InetAddress host = mServiceInfo.getHost();
                String address = host.getHostAddress();
                Log.d(Constants.TAG, "Resolved address = " + address);

                ServerInfo service = new ServerInfo(address, port);
                mServers.add(service);
            }
        };
    }
}
