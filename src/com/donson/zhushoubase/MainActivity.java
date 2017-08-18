package com.donson.zhushoubase;

import java.util.ArrayList;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.donson.receivecode.HttpCallBackListener;
import com.donson.receivecode.Item;
import com.donson.receivecode.LoginInfo;
import com.image.identification.ScreenAssistant;
import com.shell.RootShell;

public class MainActivity extends Activity implements OnClickListener, HttpCallBackListener {

	@SuppressWarnings("unused")
	private boolean isLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("Test_case");
		registerReceiver(new TestBroadcastReceiver(), myIntentFilter);

		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		System.out.println("come here ..dddd~~~~~ " + RootShell.getInstance().getConnectedType());
	}

	@Override
	public void onClick(View v) {
		// Http.get("http://sfeng.08tk.cn/sd/jc.php", null);
		// ReceiveCode receiveCode = ReceiveCode.getReceiveCode();
		// receiveCode.setListener(this);
		// if (!isLogin)
		// receiveCode.login();
		// else
		// receiveCode.getItems();
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(10000); // 切换到想要打印activity的界面
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (v == findViewById(R.id.button1)) {
				String viewName = RootShell.getInstance().getCurrentActivityName();
				if (viewName != null) {
					int startIndex = viewName.indexOf('{');
					int endIndex = viewName.indexOf('}');
					viewName = viewName.substring(startIndex + 1, endIndex);
					System.out.println("current activity name is: " + viewName);
					TextView textView = (TextView) findViewById(R.id.textView1);
					textView.setText("指定界面名称：" + viewName);
				}
			} else {
				EditText editText1 = (EditText) findViewById(R.id.editText1);
				EditText editText2 = (EditText) findViewById(R.id.editText2);
				int x = Integer.parseInt(editText2.getText().toString());
				int y = Integer.parseInt(editText1.getText().toString());
				int[][] points = { { x, y } };
				String[] colors = ScreenAssistant.getColors(points);
				if (colors != null && colors.length > 0) {
					System.out.println("point color is: " + colors[0]);
					TextView textView = (TextView) findViewById(R.id.textView1);
					textView.setText("坐标:(" + x + ", " + y + ")颜色值：" + colors[0]);
				}
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(Object response) {
		TextView textView = (TextView) findViewById(R.id.textView1);
		if (response instanceof LoginInfo) {
			isLogin = true;
			LoginInfo info = (LoginInfo) response;
			textView.setText(info.getToken());
		} else if (response instanceof ArrayList<?>) {
			StringBuilder sb = new StringBuilder();
			for (Item i : (ArrayList<Item>) response) {
				sb.append(i.getName());
				sb.append("\n");
			}
			textView.setText(sb.toString());
		}
	}

	@Override
	public void onEror(Exception e) {
		e.printStackTrace();
	}
}
