package com.donson.receivecode;

import java.util.ArrayList;

/**
 * 接口平台Api接入
 * 
 * @author Joshua
 *
 */
public class ReceiveCode implements HttpCallBackListener {

	// 单例
	private static ReceiveCode instance;
	// 平台Api接口根地址
	private static String host = "http://api.ltxin.com:8888/";
	// 登录Api接口地址
	private static String loginUrl = host + "Login?uName=donson&pWord=123456&Developer=ZvTcB8SAqO0EYw763u%2fxkQ%3d%3d";
	// 获取项目Api接口地址
	private static String getItemUrl = host + "GetItems?tp=ut";
	// 网络请求任务
	private HttpTask task;
	// 网络请求回调接口
	private HttpCallBackListener listener;
	// 登录信息
	private LoginInfo info;
	
	private ReceiveCode() {}
	
	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static ReceiveCode getReceiveCode() {
		if (instance == null)
			instance = new ReceiveCode();
		return instance;
	}
	
	/**
	 * 登录接码平台
	 */
	public void login() {
		get(loginUrl);
	}
	
	/**
	 * 获取项目
	 */
	public void getItems() {
		getItemUrl = getItemUrl + "&token=" + info.getToken();
		get(getItemUrl);
	}

	private void get(String url) {
		if (task != null)
			task.cancel();
		task = new HttpTask(url, "GET", null, "GB2312", null, this);
		task.execute();
	}

	@Override
	public void onSuccess(Object response) {
		Object res = null;
		Object[] results = (Object[]) response;

		if (results.length >= 2) {
			int code = (Integer) results[0];
			String result = results[1].toString();
			if (code == 200 && result != null) {
				if (loginUrl.equals(task.getmUrl())) {
					String[] items = result.split("&");
					if (items.length == 7) {
						info = new LoginInfo(items);
						res = info;
					}
				} else if (getItemUrl.equals(task.getmUrl())) {
					ArrayList<Item> items = new ArrayList<Item>();
					String[] itmeStrs = result.split("\n");
					for (String string : itmeStrs) {
						String[] fields = string.split("&");
						if (fields.length >= 4) {
							Item item = new Item(fields);
							if (item != null)
								items.add(item);
						}
					}
					res = items;
				}
			}
		}

		if (listener != null) {
			listener.onSuccess(res);
		}
	}

	@Override
	public void onEror(Exception e) {
		if (listener != null) {
			listener.onEror(e);
		}
	}

	public void setListener(HttpCallBackListener listener) {
		this.listener = listener;
	}
}