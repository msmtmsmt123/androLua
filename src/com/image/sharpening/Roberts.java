package com.image.sharpening;

/**
 * 一阶微分Roberts算子
 * 
 * @author Joshua
 *
 */
public class Roberts extends Sharpening {

	@Override
	public int[] processing(int[] pix, int w, int h) {
		int[] newpix = new int[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (x != w - 1 && y != h - 1) {
					newpix[x + y * w] = Math.abs(pix[x + 1 + (y + 1) * w]
							- pix[x + y * w])
							+ Math.abs(pix[x + (y + 1) * w]
									- pix[x + 1 + y * w]);
				}
			}
		}
		return super.processing(newpix, w, h);
	}
}