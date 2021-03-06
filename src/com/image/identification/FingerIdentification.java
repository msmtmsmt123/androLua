package com.image.identification;

import android.graphics.Bitmap;

import com.image.ImageDigital;

/**
 * 图像指纹
 * 
 * @author Joshua
 *
 */
public abstract class FingerIdentification implements Identification {
	/**
	 * 图片缩小后的宽
	 */
	public static final int FWIDTH = 8;
	/**
	 * 图片缩小后的高
	 */
	public static final int FHEIGHT = 8;
	/**
	 * 进行离散余弦变换的图像的宽
	 */
	public static final int DCTW = 40;
	/**
	 * 进行离散余弦变换的图像的高
	 */
	public static final int DCTH = 40;

	public static final int N = 16;

	public FingerIdentification() {
	}

	public String getCharacteristic(String srcPath) {
		Bitmap img = ImageDigital.readImg(srcPath);
		int w = img.getWidth();
		int h = img.getHeight();
		int pix[] = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		String s = getCharacteristicValue2(pix, w, h);
		return s;
	}

	/**
	 * 获得(要识别)图像的特征值，图片的指纹数
	 * 
	 * @param pix
	 *            图像的像素矩阵
	 * @param w
	 *            图像的宽
	 * @param h
	 *            图像的高
	 * @return String类型的特征值
	 */
	public String getCharacteristicValue2(int[] pix, int w, int h) {
		long l = getCharacteristicValue(pix, w, h);
		StringBuilder sb = new StringBuilder(Long.toHexString(l));
		if (sb.length() < N) {
			int n = N - sb.length();
			for (int i = 0; i < n; i++) {
				sb.insert(0, "0");
			}
		}
		return sb.toString();
	}

	/**
	 * 获得图像的特征值，由子类实现
	 * 
	 * @param pix
	 *            图像的像素矩阵
	 * @param w
	 *            图像的宽
	 * @param h
	 *            图像的高
	 * @return long类型的特征值
	 */
	public long getCharacteristicValue(int[] pix, int w, int h) {
		return 0;
	}

	public float identification(String charac1, String charc2) {
		int h = hammingDistance(charac1, charc2);
		float f = (float) Math.round(DECIMAL_PALACE * (N - h) / N)
				/ DECIMAL_PALACE;
		return f;
	}

	/**
	 * 获得图像的特征值,在数据库保存图像特征值时所用
	 * 
	 * @param srcPath
	 *            图像的像素矩阵
	 * @return long类型的特征值
	 */
	public long getCharacteristicValue4(String srcPath) {
		Bitmap img = ImageDigital.readImg(srcPath);
		int w = img.getWidth();
		int h = img.getHeight();
		int pix[] = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		long l = getCharacteristicValue(pix, w, h);
		return l;
	}

	private int hammingDistance(String s1, String s2) {
		int count = 0;
		for (int i = 0; i < s1.length(); i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				count++;
			}
		}
		return count;
	}
}