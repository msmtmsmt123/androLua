package com.donson.zhushoubase;

import com.androlua.LuaApplication;

public class BaseApplication extends LuaApplication {

	boolean isLogined = false;
	boolean isRunning = false;
	public boolean getIsLogined() {
		return isLogined;
	}

	public void setLogined(boolean isLogined) {
		this.isLogined = isLogined;
	}
	
	public void setIsRunning(boolean isRunning){
		this.isRunning = isRunning;
	}
	public boolean getIsRunning(){
		return isRunning;
	}
}
