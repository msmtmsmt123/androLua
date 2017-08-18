package com.donson.zhushoubase;
/**
 * 广播类型
 * 
 * @author Joshua
 *
 */
public enum BroadcastType {
	ShellExecStart("ShellExecStart", "开始执行Shell命令"), ShellExecFinish(
			"ShellExecFinish", "结束执行Shell命令"), RegisterAccount(
			"RegisterAccount", "注册账号");

	private String value;
	private String description;

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	private BroadcastType(String value, String description) {
		this.value = value;
		this.description = value;
	}
}