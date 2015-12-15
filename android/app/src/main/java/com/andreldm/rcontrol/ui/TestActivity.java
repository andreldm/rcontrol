package com.andreldm.rcontrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.andreldm.rcontrol.Communicator;
import com.andreldm.rcontrol.Constants;
import com.andreldm.rcontrol.R;
import com.andreldm.rcontrol.ServerDisconnectedException;

/**
 * Used for quick and dirt tests.
 */
public class TestActivity extends ActionBarActivity {
    private Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        communicator = Communicator.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // TODO still pertinent?
            return true;
        }

        if (id == R.id.action_search_devices) {
            startActivity(new Intent(this, PickServerActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPlayPause(View view) {
        try {
            communicator.send(Constants.CMD_PLAY);
        } catch (ServerDisconnectedException e) {
            startActivity(new Intent(this, PickServerActivity.class));
        }
    }

    public void onNext(View view) {
        try {
            communicator.send(Constants.CMD_NEXT);
        } catch (ServerDisconnectedException e) {
            startActivity(new Intent(this, PickServerActivity.class));
        }
    }

    public void onPrevious(View view) {
        try {
            communicator.send(Constants.CMD_PREVIOUS);
        } catch (ServerDisconnectedException e) {
            startActivity(new Intent(this, PickServerActivity.class));
        }
    }

    public void onVolUp(View view) {
        try {
            communicator.send(Constants.CMD_VOLUP);
        } catch (ServerDisconnectedException e) {
            startActivity(new Intent(this, PickServerActivity.class));
        }
    }

    public void onVolDown(View view) {
        try {
            communicator.send(Constants.CMD_VOLDOWN);
        } catch (ServerDisconnectedException e) {
            startActivity(new Intent(this, PickServerActivity.class));
        }
    }
}
