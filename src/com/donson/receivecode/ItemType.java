package com.donson.receivecode;

/**
 * 服务项目类型
 * 
 * @author Joshua
 *
 */
public enum ItemType {
	ReceiveCode(1, "些项目用于接收验证码"), SendSms(2 ,"些项目用于发送短信"), ReceiveCodeAndSendSms(3, "些项目即可接验证码，也可以发送短信"), ReceiveMultiCode(4, "些项目可以接受多个验证码");
	// 描述
	private String description;
	private int value;

	private ItemType(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	public static ItemType valueOf(int value) {
		switch (value) {
		case 1:
			return ReceiveCode;
		case 2:
			return SendSms;
		case 3:
			return ReceiveCodeAndSendSms;
		case 4:
			return ReceiveMultiCode;
		default:
			return null;
		}
	}
	
	public String getDescription() {
		return description;
	}
	
	public int value() {
		return value;
	}
}
