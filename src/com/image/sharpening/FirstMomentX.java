package com.image.sharpening;

/**
 * 水平一阶微分
 * 
 * @author Joshua
 *
 */
public class FirstMomentX extends FirstOrder {

	FirstMomentX() {
		int coefficient[] = { 1, 2, 1, 0, 0, 0, -1, -2, -1 };
		this.template = coefficient;
	}

	@Override
	public int[] processing(int[] pix, int w, int h) {
		return super.processing(pix, w, h);
	}

}