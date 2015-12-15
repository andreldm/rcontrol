package com.andreldm.rcontrol.mdns;

import android.os.AsyncTask;

public class ServiceFinderTask extends AsyncTask<Void, Void, Void> {
    private ServiceFinder mFinder;
    private ServiceFinderCallback mCallback;

    public ServiceFinderTask(ServiceFinder finder, ServiceFinderCallback callback) {
        mFinder = finder;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mFinder.startDiscovery();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mFinder.stopDiscovery();

        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        mCallback.receiveServers(mFinder.getServers());
    }
}
