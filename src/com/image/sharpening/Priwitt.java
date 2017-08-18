package com.image.sharpening;

/**
 * 一阶微分Priwitt算子
 * 
 * @author Joshua
 *
 */
public class Priwitt extends Sharpening {

	@Override
	public int[] processing(int[] pix, int w, int h) {
		int g1, g2;
		int[] newpix = new int[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (x != 0 && x != w - 1 && y != 0 && y != h - 1) {
					g1 = pix[x - 1 + (y - 1) * w] + pix[x + (y - 1) * w]
							+ pix[x + 1 + (y - 1) * w]
							- pix[x - 1 + (y + 1) * w] - pix[x + (y + 1) * w]
							- pix[x + 1 + (y + 1) * w];
					g2 = pix[x - 1 + (y - 1) * w] + pix[x - 1 + (y) * w]
							+ pix[x - 1 + (y + 1) * w]
							- pix[x + 1 + (y - 1) * w] - pix[x + 1 + (y) * w]
							- pix[x + 1 + (y + 1) * w];
					newpix[x + y * w] = (int) Math.round(Math.sqrt(g1 * g1 + g2
							* g2));
				}
			}
		}
		return super.processing(newpix, w, h);
	}
}