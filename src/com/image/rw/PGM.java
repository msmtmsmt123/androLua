package com.image.rw;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class PGM extends ReadImage {
	private char ch0, ch1;
	private int maxpix;

	/*
	 * private DataInputStream in = null; private BufferedImage img = null;
	 */
	public PGM(String srcPath) {
		super(srcPath);
		readPGMHeader();
	}

	public void readPGMHeader() {
		try {
			ch0 = (char) in.readByte();
			ch1 = (char) in.readByte();
			if (ch0 != 'P' || ch1 != '5') {
				System.out.print("Not a pgm image!" + " [0]=" + ch0 + ", [1]="
						+ ch1);
				System.exit(0);
			}
			in.readByte(); // 读空格
			char c = (char) in.readByte();

			if (c == '#') // 读注释行
			{
				do {
					c = (char) in.readByte();
				} while ((c != '\n') && (c != '\r'));
				c = (char) in.readByte();
			}

			// 读出宽度
			if (c < '0' || c > '9') {
				System.out.print("Errow!");
				System.exit(1);
			}

			int k = 0;
			do {
				k = k * 10 + c - '0';
				c = (char) in.readByte();
			} while (c >= '0' && c <= '9');
			width = k;

			// 读出高度
			c = (char) in.readByte();
			if (c < '0' || c > '9') {
				System.out.print("Errow!");
				System.exit(1);
			}

			k = 0;
			do {
				k = k * 10 + c - '0';
				c = (char) in.readByte();
			} while (c >= '0' && c <= '9');
			height = k;

			// 读出灰度最大值(尚未使用)
			c = (char) in.readByte();
			if (c < '0' || c > '9') {
				System.out.print("Errow!");
				System.exit(1);
			}

			k = 0;
			do {
				k = k * 10 + c - '0';
				c = (char) in.readByte();
			} while (c >= '0' && c <= '9');
			maxpix = k;
		} catch (IOException e1) {
			System.out.println("Exception!");
		}
	}

	/***************************************************************
	 * 读入.pgm或.ppm文件 type 5:pgm, 6:ppm
	 ***************************************************************/
	public Bitmap readImage() {
		int[] pixels = new int[width * height];
		System.out.println(ch1 + "  " + pixels.length);
		try {
			for (int i = 0; i < width * height; i++) {
				int b = in.readByte();
				if (b < 0)
					b = b + 256;
				pixels[i] = (255 << 24) | (b << 16) | (b << 8) | b;
			}
		} catch (IOException e1) {
			System.out.println("Exception!");
			e1.printStackTrace();
		}
		if (img == null) {
			img = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		}
		img.setPixels(pixels, 0, width, 0, 0, width, height);
		return img;
	}

	public char getCh0() {
		return ch0;
	}

	public char getCh1() {
		return ch1;
	}

	public int getWidth() {
		return width;
	}
}