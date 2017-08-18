package com.image.identification;

import android.graphics.Bitmap;

import com.image.ImageDigital;

/**
 * 
 * @author Joshua
 *
 */
public class HistogramIdentification implements Identification {
	/**
	 * 表示R、G、B的位数
	 */
	public static final int GRAYBIT = 4;

	public HistogramIdentification() {
	}

	/**
	 * 求一维的灰度直方图
	 * 
	 * @param img
	 * @return
	 */
	public String getHistogram(Bitmap img) {
		int w = img.getWidth();
		int h = img.getHeight();
		int series = (int) Math.pow(2, GRAYBIT); // GRAYBIT=4;用12位的int表示灰度值，前4位表示red,中间4们表示green,后面4位表示blue
		int greyScope = 256 / series;
		float[] hist = new float[series * series * series];
		int r, g, b, index;
		int pix[] = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		for (int i = 0; i < w * h; i++) {
			r = pix[i] >> 16 & 0xff;
			r = r / greyScope;
			g = pix[i] >> 8 & 0xff;
			g = g / greyScope;
			b = pix[i] & 0xff;
			b = b / greyScope;
			index = r << (2 * GRAYBIT) | g << GRAYBIT | b;
			hist[index]++;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hist.length; i++) {
			hist[i] = hist[i] / (w * h);
			hist[i] = (float) Math.round(hist[i] * DECIMAL_PALACE)
					/ DECIMAL_PALACE;
			sb.append(hist[i]);
			if (i != hist.length - 1) {
				sb.append("_");
			}
		}
		return sb.toString();
	}

	/**
	 * 基于一维灰度直方图特征的图像匹配
	 * 
	 * @param histR
	 * @param histD
	 * @return
	 */
	public static float indentification(float[] histR, float[] histD) {
		float p = (float) 0.0;
		for (int i = 0; i < histR.length; i++) {
			p += Math.sqrt(histR[i] * histD[i]);
		}
		p = (float) Math.round(p * DECIMAL_PALACE) / DECIMAL_PALACE;
		return p;
	}

	private float[] convertFloat(String charact) {
		String strs[] = charact.split("_");
		float histo[] = new float[strs.length];
		for (int i = 0; i < strs.length; i++) {
			histo[i] = Float.parseFloat(strs[i]);
		}
		return histo;
	}

	public float identification(String charac1, String charc2) {
		float[] histR = convertFloat(charac1);
		float[] histD = convertFloat(charc2);
		return indentification(histR, histD);
	}

	public String getCharacteristic(String srcPath) {
		Bitmap img = ImageDigital.readImg(srcPath);
		return getHistogram(img);
	}
}