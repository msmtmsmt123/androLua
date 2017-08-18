package com.image;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BMPReader2 {
	/**
	 * 不压缩
	 */
	public static final int CM_NONE = 0;
	/**
	 * RLE8压缩
	 */
	public static final int CM_RLE8 = 1;
	/**
	 * RLE4压缩
	 */
	public static final int CM_RLE4 = 2;

	private int bfSize; // BMP图像文件的大小
	private int biSizeImage;// BMP图像数据大小
	private int width; // BMP图像的宽度
	private int height; // BMP图像的高度
	private int biBitCount; // BMP图像的色深，即一个像素用多少位表示
	private int colorTable[];// BMP图像的调色板

	private int biCompression; // 压缩方式
	private int biClrUsed; // 使用的颜色
	private int biClrImportant; // 重要的颜色数
	private DataInputStream in = null;

	public BMPReader2(DataInputStream in) {
		this.in = in;
		readFileHead(in);
		readInfoHead(in);
	}

	public BMPReader2(String srcPath) {
		try {
			this.in = new DataInputStream(new FileInputStream(srcPath));
			readFileHead(in);
			readInfoHead(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void readFileHead(DataInputStream in) {
		try {
			// 判断是否为MMP图像
			if (in.read() != 'B') {
				throw new IOException("Not a .BMP file!");
			}
			if (in.read() != 'M') {
				throw new IOException("Not a .BMP file!");
			}
			bfSize = transInt(in.readInt());
			// 两个保留值
			in.readUnsignedShort();
			in.readUnsignedShort();
			int bfOffBits = transInt(in.readInt());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readInfoHead(DataInputStream in) {
		try {
			int biSize = transInt(in.readInt());
			width = transInt(in.readInt());
			height = transInt(in.readInt());
			in.readUnsignedShort();
			biBitCount = transShort(in.readUnsignedShort());
			biCompression = transInt(in.readInt());
			biSizeImage = transInt(in.readInt());
			in.readInt();
			in.readInt();
			biClrUsed = transInt(in.readInt()) + 1;
			biClrImportant = transInt(in.readInt());
			if (biClrUsed == 0) {
				biClrUsed = 1 << biBitCount;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取调色版 已验证正确
	 * 
	 * @param in
	 * @return
	 */
	public int[] readColorTable(DataInputStream in) {
		colorTable = new int[biClrUsed];
		for (int i = 0; i < biClrUsed; i++) {
			try {
				colorTable[i] = (transInt(in.readInt()) & 0xffffff) | 0xff000000;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return colorTable;
	}

	public int[] readData() throws IOException {
		int pixs[] = new int[width * height];
		if (biBitCount <= 8) {
			readColorTable(in);
		}
		if (CM_NONE == biCompression) {
			if (biBitCount == 8) {
				System.out.println("is 8 bit");
				readColorTable(in);
				readRGB8(in, width, height, pixs, biSizeImage);
			} else if (biBitCount == 16) {
				readRGB16(in, width, height, pixs);
			} else if (biBitCount == 24) {
				readRGB24(in, width, height, pixs);
			} else if (biBitCount == 32) {
				readGRB32(in, width, height, pixs);
			} else {
				readColorTable(in);
				System.out.println("bit:" + biBitCount + "  len:"
						+ colorTable.length);
				try {
					readRGB(width, height, colorTable, biBitCount, pixs, in);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (CM_RLE8 == biCompression) {
			System.out.println("is CM_RLE8");
			readColorTable(in);
			System.out.println("clor Len:" + colorTable.length);
			readRLE8(width, height, colorTable, biBitCount, pixs, in,
					biSizeImage);
		} else if (CM_RLE4 == biCompression) {
			System.out.println("is CM_RLE4");

			readColorTable(in);
			System.out.println("clor Len:" + colorTable.length);
		}
		return pixs;
	}

	private void readRGB8(DataInputStream in, int w, int h, int[] pixs,
			int biSizeImage) {
		int c, r, g, b;
		for (int y = h - 1; y >= 0; y--) {
			for (int x = 0; x < w; x++) {
				try {
					c = in.read();
					if (c != -1) {
						pixs[y * w + x] = colorTable[c];
					} else {
						pixs[y * w + x] = colorTable[0];
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readRGB16(DataInputStream in, int w, int h, int[] pixs) {
		int c, r, g, b;
		for (int y = h - 1; y >= 0; y--) {
			for (int x = 0; x < w; x++) {
				try {
					c = transShort(in.readShort());
					r = ((c >> 10) & 0x1f) << 3; // x<<3 = x*8
					g = ((c >> 5) & 0x1f) << 3;
					b = (c & 0x1f) << 3;
					pixs[(y) * w + x] = 0xff << 24 | r << 16 | g << 8 | b;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readRGB24(DataInputStream in, int w, int h, int[] pixs) {
		int r, g, b;
		for (int y = h - 1; y >= 0; y--) {
			for (int x = 0; x < w; x++) {
				try {
					b = in.read();
					g = in.read();
					r = in.read();
					pixs[(y) * w + x] = 0xff << 24 | r << 16 | g << 8 | b;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readGRB32(DataInputStream in, int w, int h, int[] pixs) {
		int a, r, g, b;
		for (int y = h - 1; y >= 0; y--) {
			for (int x = 0; x < w; x++) {
				try {
					b = in.read();
					g = in.read();
					r = in.read();
					a = in.read();
					pixs[(y) * w + x] = a << 24 | r << 16 | g << 8 | b;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readRLE8(int w, int h, int[] colorTable, int biBitCount,
			int[] pixs, DataInputStream in, int biSizeImage) {
		int x = 0;
		int y = h - 1;

		int i = 0;
		while (i < biSizeImage) {
			try {
				/**
				 * 00 00 End of scan line 00 01 End of bitmap data 00 02 XX YY
				 * Run offset marker
				 */
				int byte1 = in.read();
				int byte2 = in.read();
				i += 2;

				if (byte1 == 0) {
					if (byte2 == 0) {
						y -= 1;
						x = 0;
					} else if (byte2 == 1) {
						return;
					} else if (byte2 == 2) {
						int xoff = in.read();
						i++;
						int yoff = in.read();
						i++;
						x += xoff;
						y -= yoff;
					} else {
						int currentByte = 0;
						for (int j = 0; j < byte2; j++) {
							currentByte = in.read();
							pixs[y * w + x] = colorTable[currentByte];
							i++;
							x++;
							if (x >= width) {
								x = 0;
								y--;
							}
						}
					}
				} else {
					int currentByte = 0;
					for (int j = 0; j < byte1; j++) {
						pixs[y * w + x] = colorTable[byte2];
						i++;
						x++;
						if (x >= width) {
							x = 0;
							y--;
						}
					}
				}

				// i++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("i:" + i);
	}

	// readRGB reads in pixels values that are stored uncompressed. The bits
	// represent
	// indices into the color table
	protected static void readRGB(int width, int height, int colorTable[],
			int bitCount, int pixels[], DataInputStream in) throws IOException {
		// How many pixels can be stored in a byte?
		int pixelsPerByte = 8 / bitCount;

		// A bit mask containing the number of bits in a pixel
		int bitMask = (1 << bitCount) - 1;

		// The shift values that will move each pixel to the far right
		int bitShifts[] = new int[pixelsPerByte];

		for (int i = 0; i < pixelsPerByte; i++)
			bitShifts[i] = 8 - ((i + 1) * bitCount);

		int whichBit = 0;

		// Read in the first byte
		int currByte = in.read();

		// Start at the bottom of the pixel array and work up
		for (int h = height - 1; h >= 0; h--) {
			int pos = h * width;
			for (int w = 0; w < width; w++) {
				// Get the next pixel from the current byte
				pixels[pos] = colorTable[(currByte >> bitShifts[whichBit])
						& bitMask];
				pos++;
				whichBit++;

				// If the current bit position is past the number of pixels in
				// a byte, you advance to the next byte
				if (whichBit >= pixelsPerByte) {
					whichBit = 0;
					currByte = in.read();
				}
			}
		}
	}

	protected int transInt(int i) {
		return ((i & 0xff) << 24 | (i & 0xff00) << 8 | (i & 0xff0000) >> 8 | (i >> 24) & 0xff);
	}

	protected int transShort(int i) {
		return (i & 0xff) << 8 | (i >> 8) & 0xff;
	}

	public int getBfSize() {
		return bfSize;
	}

	public int getBiSizeImage() {
		return biSizeImage;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBiBitCount() {
		return biBitCount;
	}

	public int[] getColorTable() {
		return colorTable;
	}
}