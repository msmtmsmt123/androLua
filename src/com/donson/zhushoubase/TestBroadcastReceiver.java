package com.donson.zhushoubase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TestBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		System.out.println("action name is: " + action);
		Toast.makeText(context, "action name is: " + action + " data is: " + intent.getStringExtra("content"), Toast.LENGTH_LONG).show();
	}

}