package com.image.filtering;

/**
 * 均值滤波
 * 
 * @author Joshua
 *
 */
public class AverageFiltering extends Filtering {
	@Override
	public int[] processing(int pix[], int w, int h) {
		int newpix[] = new int[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (x != 0 && x != w - 1 && y != 0 && y != h - 1) {
					newpix[y * w + x] = Math
							.round((pix[x - 1 + (y - 1) * w]
									+ pix[x + (y - 1) * w]
									+ pix[x + 1 + (y - 1) * w]
									+ pix[x - 1 + (y) * w] + pix[x + (y) * w]
									+ pix[x + 1 + (y) * w]
									+ pix[x - 1 + (y + 1) * w]
									+ pix[x + (y + 1) * w] + pix[x + 1
									+ (y + 1) * w]) / 9);
				} else {
					newpix[y * w + x] = pix[y * w + x];
				}
			}
		}
		return super.processing(newpix, w, h);
	}
}