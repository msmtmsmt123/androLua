package com.image.filtering;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.image.ImageDecorator;
import com.image.ImageDigital;

/**
 * 过滤器
 * 
 * @author Joshua
 *
 */
public abstract class Filtering extends ImageDecorator {

	@Override
	public int[] processing(int[] pix, int w, int h) {
		return super.processing(pix, w, h);
	}

	/**
	 * 滤波
	 * 
	 * @param srcPath
	 *            图片的存储位置
	 * @param destPath
	 *            图像要保存的存储位置
	 * @param format
	 *            图像要保存的存储位置
	 */
	public void processing(String srcPath, String destPath,
			CompressFormat format) {
		Bitmap img = ImageDigital.readImg(srcPath);
		int w = img.getWidth();
		int h = img.getHeight();
		int[] pix = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		for (int i = 0; i < w * h; i++) {
			pix[i] = pix[i] & 0xff;
		}
		int newpix[] = processing(pix, w, h);
		for (int i = 0; i < w * h; i++) {
			newpix[i] = 255 << 24 | newpix[i] << 16 | newpix[i] << 8
					| newpix[i];
		}
		img.setPixels(newpix, 0, w, 0, 0, w, h);
		ImageDigital.writeImg(img, format, destPath);
	}
}