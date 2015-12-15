package com.andreldm.rcontrol.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.andreldm.rcontrol.Communicator;
import com.andreldm.rcontrol.R;
import com.andreldm.rcontrol.mdns.ServerInfo;
import com.andreldm.rcontrol.mdns.ServiceFinder;
import com.andreldm.rcontrol.mdns.ServiceFinderCallback;
import com.andreldm.rcontrol.mdns.ServiceFinderTask;

import java.util.List;

public class PickServerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        ServiceFinder mServiceFinder= new ServiceFinder(this);

        final Context context = new ContextThemeWrapper(this, R.style.AppTheme);

        final ProgressDialog dialog = ProgressDialog
                .show(context, "Looking up", "Looking up servers, please wait...", true);

        new ServiceFinderTask(mServiceFinder, new ServiceFinderCallback() {
            @Override
            public void receiveServers(List<ServerInfo> servers) {
                dialog.dismiss();

                if (servers == null || servers.isEmpty()) {
                    Toast.makeText(PickServerActivity.this, "No server was found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                pickServer(context, servers);
            }
        }).execute();
    }

    private void pickServer(Context context, final List<ServerInfo> servers) {
        final ArrayAdapter<ServerInfo> adapter = new ArrayAdapter<>(
                context, android.R.layout.select_dialog_item, servers);

        AlertDialog dialog = new AlertDialog.Builder(context)
        .setTitle("Pick a server")
        .setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, final int which) {
                updateCommunicator(servers.get(which));
                finish();
            }
        })
        .create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }

    private void updateCommunicator(final ServerInfo server) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Communicator.getInstance().setServer(server);
            }
        });
    }
}
