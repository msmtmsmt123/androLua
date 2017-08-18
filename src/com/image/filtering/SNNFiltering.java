package com.image.filtering;

/**
 * 对称近邻均值滤波
 * 
 * @author Joshua
 *
 */
public class SNNFiltering extends Filtering {

	@Override
	public int[] processing(int[] pix, int w, int h) {
		int newpix[] = new int[w * h];
		int N = 5; // N = 3,5,7,9,11...
		int n = N * N;
		int temp, i1, i2, sum;
		int[] temp1 = new int[n];
		int[] temp2 = new int[n / 2];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (x >= N / 2 && x < w - N / 2 && y >= N / 2 && y < h - N / 2) {
					int x0 = x - N / 2;
					int y0 = y - N / 2;
					for (int j = 0; j < N; j++) {
						for (int i = 0; i < N; i++) {
							temp1[i * N + j] = pix[x0 + i + (y0 + j) * w];
						}
					}
					sum = 0;
					for (int k = 0; k < n / 2; k++) {
						i1 = Math.abs(temp1[n / 2] - temp1[k]);
						i2 = Math.abs(temp1[n / 2] - temp1[n - k - 1]);
						temp2[k] = i1 < i2 ? temp1[k] : temp1[n - k - 1]; // 选择最接近原像素值的一个邻近像素
						sum = sum + temp2[k];
					}
					newpix[y * w + x] = sum / (n / 2);
				} else {
					newpix[y * w + x] = pix[y * w + x];
				}
			}
		}
		return super.processing(newpix, w, h);
	}
}