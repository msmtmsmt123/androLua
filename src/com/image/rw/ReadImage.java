package com.image.rw;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 读取图片
 * 
 * @author Joshua
 *
 */
public abstract class ReadImage {
	protected int width; // BMP图像的宽度
	protected int height; // BMP图像的高度
	protected DataInputStream in = null;
	protected Bitmap img = null;

	public ReadImage(String srcPath) {
		try {
			in = new DataInputStream(new FileInputStream(srcPath));
			img = BitmapFactory.decodeFile(srcPath);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public abstract Bitmap readImage();

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}