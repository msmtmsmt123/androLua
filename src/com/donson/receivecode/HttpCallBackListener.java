package com.donson.receivecode;

/**
 * 网络请求回调接口
 * 
 * @author Joshua
 *
 */
public interface HttpCallBackListener {
	void onSuccess(Object response);
	void onEror(Exception e);
}
