package com.image;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.image.rw.BMP;
import com.image.rw.PGM;
import com.image.rw.PPM;
import com.image.rw.ReadImage;

/**
 * 数字图像处理的（常用）功能类
 * 
 * @author Joshua
 *
 */
public class ImageDigital {
	private static Bitmap img = null;

	/**
	 * 读取图片
	 * 
	 * @param srcPath
	 *            图片的存储位置
	 * @return 返回图片的BufferedImage对象
	 */
	public static Bitmap readImg(String srcPath) {
		ReadImage readImg = null;
		DataInputStream in = null;
		try {
			in = new DataInputStream(new FileInputStream(srcPath));
			char ch1 = (char) in.read();
			char ch2 = (char) in.read();
			if (ch1 == 'B' && ch2 == 'M') {
				// System.out.println("BMP");
				readImg = new BMP(srcPath);
				img = readImg.readImage();
			} else if (ch1 == 'P' && ch2 == '5') {
				// System.out.println("PGM");
				readImg = new PGM(srcPath);
				img = readImg.readImage();
			} else if (ch1 == 'P' && ch2 == '6') {
				readImg = new PPM(srcPath);
				img = readImg.readImage();
			} else if (ch1 == 'R' && ch2 == ' ') {
				// readImg = new PPM(srcPath);
			} else {
				img = BitmapFactory.decodeFile(srcPath);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}

	/**
	 * 读取图片
	 * 
	 * @param srcPath
	 *            图片的存储位置
	 * @return 返回图像的矩阵数组
	 */
	public static int[] readImg2(String srcPath) {
		img = ImageDigital.readImg(srcPath);
		int w = img.getWidth();
		int h = img.getHeight();
		int pix[] = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		return pix;
	}

	/**
	 * 将图片写入磁盘
	 * 
	 * @param img
	 *            图像的Bitmap对象
	 * @param formatName
	 *            存储的文件格式
	 * @param destPath
	 *            图像要保存的存储位置
	 */
	public static void writeImg(Bitmap img, CompressFormat formatName,
			String destPath) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(destPath);
			img.compress(formatName, 100, out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将图片写入磁盘
	 * 
	 * @param pix
	 *            图像的矩阵数组
	 * @param w
	 *            图像的宽
	 * @param h
	 *            图像的高
	 * @param formatName
	 *            存储的文件格式
	 * @param destPath
	 *            图像要保存的存储位置
	 */
	public static void writeImg(int pix[], int w, int h, CompressFormat formatName,
			String destPath) {
		img.setPixels(pix, 0, w, 0, 0, w, h);
		writeImg(img, formatName, destPath);
	}

	/**
	 * 将图片转化成黑白灰度图片,返回的是一维的像素矩阵，而不是像素值
	 * 
	 * @param pix
	 *            保存图片像素
	 * @param w
	 *            二维像素矩阵的宽
	 * @param h
	 *            二维像素矩阵的高
	 * @return 灰度矩阵,
	 */
	public static int[] grayImage(int pix[], int w, int h) {
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				pix[i * w + j] = (int) (0.299 * Color.red(pix[i * w + j])
						+ 0.587 * Color.green(pix[i * w + j]) + 0.114 * Color
						.blue(pix[i * w + j]));

			}
		}
		return pix;
	}

	/**
	 * 将图片转化成黑白灰度图片
	 * 
	 * @param srcPath
	 *            源图像的路径
	 * @param format
	 *            图像的格式
	 * @param destPath
	 *            图像要保存的存储位置
	 */
	public static void grayImage(String srcPath, CompressFormat format, String destPath) {
		img = readImg(srcPath);
		int w = img.getWidth();
		int h = img.getHeight();
		int pix[] = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		int[] newPix = new int[w * h];
		int c = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				c = (int) (0.299 * Color.red(pix[i * w + j]) + 0.587
						* Color.green(pix[i * w + j]) + 0.114 * Color
						.blue(pix[i * w + j]));
				newPix[i * w + j] = 255 << 24 | c << 16 | c << 8 | c;
			}
		}
		img.setPixels(newPix, 0, w, 0, 0, w, h);
		writeImg(img, format, destPath);
	}

	/**
	 * 将图片分离出红色灰度矩阵
	 * 
	 * @param pix
	 *            保存图片像素
	 * @param w
	 *            二维像素矩阵的宽
	 * @param h
	 *            二维像素矩阵的高
	 * @return 红色灰度矩阵
	 */
	public static int[] splitRed(int[] pix, int w, int h) {
		int rPix[] = new int[w * h];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				rPix[i * w + j] = Color.red(pix[i * w + j]);
			}
		}
		return rPix;
	}

	/**
	 * 将图片转化成红色灰度图片
	 * 
	 * @param srcPath
	 *            源图像的路径
	 * @param format
	 *            图像的格式
	 * @param destPath
	 *            图像要保存的存储位置
	 */
	public static void splitRed(String srcPath, CompressFormat format, String destPath) {
		img = readImg(srcPath);
		int w = img.getWidth();
		int h = img.getHeight();
		int pix[] = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		int[] rPix = new int[w * h];
		int c = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				c = Color.red(pix[i * w + j]);
				rPix[i * w + j] = 255 << 24 | c << 16 | 0 << 8 | 0;
			}
		}
		img.setPixels(pix, 0, w, 0, 0, w, h);
		writeImg(img, format, destPath);
	}

	/**
	 * 将图片分离出绿色灰度矩阵
	 * 
	 * @param pix
	 *            保存图片像素
	 * @param w
	 *            二维像素矩阵的宽
	 * @param h
	 *            二维像素矩阵的高
	 * @return 绿色灰度矩阵
	 */
	public static int[] splitGreen(int[] pix, int w, int h) {
		int gPix[] = new int[w * h];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				gPix[i * w + j] = Color.green(pix[i * w + j]);
			}
		}
		return gPix;
	}

	/**
	 * 将图片转化成绿色灰度图片
	 * 
	 * @param srcPath
	 *            源图像的路径
	 * @param format
	 *            图像的格式
	 * @param destPath
	 *            图像要保存的存储位置
	 */
	public static void splitGreen(String srcPath, CompressFormat format, String destPath) {
		img = readImg(srcPath);
		int w = img.getWidth();
		int h = img.getHeight();
		int pix[] = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		int[] gPix = new int[w * h];
		int c = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				c = Color.green(pix[i * w + j]);
				gPix[i * w + j] = 255 << 24 | 0 << 16 | c << 8 | 0;
			}
		}
		img.setPixels(gPix, 0, w, 0, 0, w, h);
		writeImg(img, format, destPath);
	}

	/**
	 * 将图片分离出蓝色灰度矩阵
	 * 
	 * @param pix
	 *            保存图片像素
	 * @param w
	 *            二维像素矩阵的宽
	 * @param h
	 *            二维像素矩阵的高
	 * @return 蓝色灰度矩阵
	 */
	public static int[] splitBlue(int[] pix, int w, int h) {
		int gPix[] = new int[w * h];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				gPix[i * w + j] = Color.blue(pix[i * w + j]);
			}
		}
		return gPix;
	}

	/**
	 * 将图片转化成蓝色灰度图片
	 * 
	 * @param srcPath
	 *            源图像的路径
	 * @param format
	 *            图像的格式
	 * @param destPath
	 *            图像要保存的存储位置
	 */
	public static void splitBlue(String srcPath, CompressFormat format, String destPath) {
		img = readImg(srcPath);
		int w = img.getWidth();
		int h = img.getHeight();
		int pix[] = new int[w * h];
		img.getPixels(pix, 0, w, 0, 0, w, h);
		int[] gPix = new int[w * h];
		int c = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				c = Color.blue(pix[i * w + j]);
				gPix[i * w + j] = 255 << 24 | 0 << 16 | 0 << 8 | c;
			}
		}
		img.setPixels(gPix, 0, w, 0, 0, w, h);
		writeImg(img, format, destPath);
	}
}