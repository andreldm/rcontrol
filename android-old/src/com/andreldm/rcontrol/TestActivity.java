package com.andreldm.rcontrol;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
	}

    public void onClick(View view) 
    {
    	Communicator.getInstance().sendBackward();
    }
}
