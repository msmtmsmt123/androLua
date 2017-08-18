package com.donson.receivecode;

/**
 * 打码平台登录信息
 * 
 * @author Joshua
 *
 */
public class LoginInfo {

	// 登录token
	private String token;
	// 账户余额
	private float over;
	// 最大登录客户端个数
	private int maxClientNum;
	// 最多获取号码数
	private int maxNoNum;
	// 单个客户端最多获取号码数
	private int sinCliMaxNoNum;
	// 折扣
	private float discount;
	
	public String getToken() {
		return token;
	}

	public float getOver() {
		return over;
	}

	public int getMaxClientNum() {
		return maxClientNum;
	}

	public int getMaxNoNum() {
		return maxNoNum;
	}

	public int getSinCliMaxNoNum() {
		return sinCliMaxNoNum;
	}

	public float getDiscount() {
		return discount;
	}

	public LoginInfo(String [] fields) {
		if (fields.length < 6) return;
		this.token = fields[0];
		this.over = Float.valueOf(fields[1]);
		this.maxClientNum = Integer.valueOf(fields[2]);
		this.maxNoNum = Integer.valueOf(fields[3]);
		this.sinCliMaxNoNum = Integer.valueOf(fields[4]);
		this.discount = Float.valueOf(fields[5]);
	}
	
}
