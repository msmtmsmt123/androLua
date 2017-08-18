package com.image.rw;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RAW {
	// 将image编码为RAW图像,返回byte[][]
	public byte[][] readRAW_DAT(String name, int iw, int ih) {
		byte[][] pix = new byte[iw][ih];
		try {
			FileInputStream fin = new FileInputStream(name);
			DataInputStream in = new DataInputStream(fin);

			// 磁盘文件读入数据
			for (int j = 0; j < ih; j++) {
				for (int i = 0; i < iw; i++) {
					byte c = in.readByte();
					pix[i][j] = c;
				}
			}
		} catch (IOException e1) {
		}

		return pix;
	}

	// 将image编码为RAW图像, 返回int[][]
	public int[] readRAW(String name, int iw, int ih) {
		int c;
		int[] pix = new int[iw * ih];

		try {
			FileInputStream fin = new FileInputStream(name);
			DataInputStream in = new DataInputStream(fin);

			// 磁盘文件读入数据
			for (int j = 0; j < ih; j++) {
				for (int i = 0; i < iw; i++) {
					c = in.readByte();
					if (c < 0)
						c += 256;
					pix[i + j * iw] = (255 << 24) | (c << 16) | (c << 8) | c;
				}
			}
		} catch (IOException e1) {
			System.out.println("Exception!");
		}
		return pix;
	}

	// 读入.raw文件
	public int[] readRAW1D(String name, int iw, int ih) {
		int[] pix = new int[iw * ih];
		try {
			FileInputStream fin = new FileInputStream(name);
			DataInputStream in = new DataInputStream(fin);

			for (int i = 0; i < iw * ih; i++) { // 磁盘文件读入数据
				int c = in.readByte();
				if (c < 0)
					c = c + 256;
				pix[i] = c;
			}
		} catch (IOException e1) {
			System.out.println("Exception!");
		}
		return pix;
	}

	// 在当前目录写入.raw文件
	public void writeRAW(String name, int[] pix, int iw, int ih) {
		try {
			RandomAccessFile rf = new RandomAccessFile(name, "rw");
			for (int i = 0; i < iw * ih; i++) {
				rf.writeByte((byte) (pix[i] & 0xff));
			}
			rf.close();
		} catch (FileNotFoundException e1) {
		} catch (IOException e2) {
		}
	}
}