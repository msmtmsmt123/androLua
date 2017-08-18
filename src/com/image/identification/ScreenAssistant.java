package com.image.identification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.util.SparseIntArray;
import com.androlua.LuaApplication;
import com.image.ImageDigital;
import com.shell.RootShell;

/**
 * 查找颜色、图片、文字
 * 
 * @author Joshua
 *
 */
public class ScreenAssistant {

	private static int maxWaitForGennerFileTime = 10; // 等待生成截图文件的最大次数
	private static String screenshotPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "screenshot.png"; // Environment.getExternalStorageDirectory().getPath()
	private static String[] binaryArray = { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111" }; // 十六进制对应二进制字符串

	/**
	 * 多点取色
	 * 
	 * @param args
	 *            要找色的多个点坐标
	 * @return 多点的颜色值数组
	 */
	public static String[] getColors(int[][] points) {
		if (points != null) {
			screenCap(screenshotPath);
			int count = points.length;
			String[] colors = new String[count];
			Bitmap bm = BitmapFactory.decodeFile(screenshotPath);
			if (bm != null) {
				RootShell shell = RootShell.getInstance();
				for (int i = 0; i < count; i++) {
					int color = bm.getPixel((int)(points[i][0] * shell.widthRatio), (int)(points[i][1] * shell.heightRatio));
					colors[i] = Integer.toHexString(color);
				}
			}
			return colors;
		}
		return null;
	}

	/**
	 * 区域找色，返回查找到的颜色坐标，当颜色多个时，查找到第一个就返回
	 * 
	 * @param rect
	 *            要查找的区域范围
	 * @param colors
	 *            要查找的颜色列表
	 * @param sim
	 *            相似度 取值范围：0~1.0
	 * @return 颜色坐标
	 */
	public static Point findColor(int[] rect, String[][] colors, double sim) {
		if (colors == null || colors.length == 0) {
			return null;
		}
		if (sim <= 0)
			sim = 0.9;
		if (sim > 1.0)
			sim = 1.0;

		SparseIntArray colorArray = new SparseIntArray();
		for (String[] colorAndOffset : colors) {
			if (colorAndOffset == null || colorAndOffset.length == 0)
				continue;
			String color = colorAndOffset[0];
			if (color == null || "".equals(color.trim()))
				continue;
			int value = 0;
			if (colorAndOffset.length == 2) {
				String offset = colorAndOffset[1];
				if (offset != null && !"".equals(offset.trim()))
					value = Integer.valueOf(offset, 16);
			}
			int key = Integer.valueOf(color, 16);
			colorArray.put(key, value);
		}

		screenCap(screenshotPath);

		Bitmap bitmap = ImageDigital.readImg(screenshotPath);
		if (bitmap == null)
			return null;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Rect searchRect = null;
		if (rect != null && rect.length == 4) {
			RootShell shell = RootShell.getInstance();
			searchRect = new Rect((int)(rect[0] * shell.widthRatio), (int)(rect[1] * shell.heightRatio), (int)(rect[2] * shell.widthRatio), (int)(rect[3] * shell.heightRatio));
		} else {
			searchRect = new Rect(0, 0, width, height);
		}
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int i = searchRect.top; i < searchRect.bottom; i++) {
			for (int j = searchRect.left; j < searchRect.right; j++) {
				int index = i * width + j;
				int pixel = pixels[index];
				for (int k = 0; k < colorArray.size(); k++) {
					Boolean match = compareColor(pixel, colorArray.keyAt(k), colorArray.valueAt(k), sim, CompareSim.ColorAlikeDeg);
					if (match)
						return new Point(j, i);
				}
			}
		}

		return null;
	}

	/**
	 * 多点找色
	 * 
	 * @param rect
	 *            查找区域范围
	 * @param colorStrs
	 *            要查找的颜色
	 * @param offsetColorStrs
	 *            查找的颜色点相邻的颜色
	 * @param sim
	 *            相似度
	 * @return
	 */
	public static Point findMultiColor(int[] rect, String colorStr, String offsetColorStr, double sim) {
		if (colorStr == null || "".equals(colorStr.trim()))
			return null;
		if (sim <= 0)
			sim = 0.9;
		if (sim > 1.0)
			sim = 1.0;

		// 解释要查找的颜色
		ArrayList<int[]> colors = new ArrayList<int[]>();
		String[] colorStrs = colorStr.split("\\|");
		for (String color : colorStrs) {
			if (color == null || "".equals(color.trim()))
				continue;
			String[] colorunits = color.split("-");
			if (colorunits.length > 0) {
				int[] co = new int[2];
				co[0] = Integer.valueOf(colorunits[0], 16);
				co[1] = colorunits.length == 2 ? Integer.valueOf(colorunits[1], 16) : 0;
				colors.add(co);
			}
		}

		// 解释偏离颜色
		ArrayList<int[][]> offsetColors = new ArrayList<int[][]>();
		String[] offsetColorStrs = offsetColorStr.split(",");
		for (String offsetColor : offsetColorStrs) {
			if (offsetColor == null || "".equals(offsetColor.trim()))
				continue;
			String[] offsetColorUnit = offsetColor.split("\\|");
			if (offsetColorUnit.length > 2) {
				int[][] offset = new int[offsetColorUnit.length - 1][2];
				RootShell shell = RootShell.getInstance();
				offset[0][0] = (int)(Integer.valueOf(offsetColorUnit[0]) * shell.widthRatio); // x
				offset[0][1] = (int)(Integer.valueOf(offsetColorUnit[1]) * shell.heightRatio); // y
				for (int i = 1; i <= offsetColorUnit.length - 2; i++) {
					String[] offsetColorUnits = offsetColorUnit[i + 1].split("-");
					if (offsetColorUnits.length > 0) {
						offset[i][0] = Integer.valueOf(offsetColorUnits[0], 16); // color
						offset[i][1] = offsetColorUnits.length == 2 ? Integer.valueOf(offsetColorUnits[1], 16) : 0; // offset color
					}
				}
				offsetColors.add(offset);
			}
		}

		screenCap(screenshotPath);

		Bitmap bitmap = ImageDigital.readImg(screenshotPath);
		if (bitmap == null)
			return null;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Rect searchRect = null;
		if (rect != null && rect.length == 4) {
			RootShell shell = RootShell.getInstance();
			searchRect = new Rect((int)(rect[0] * shell.widthRatio), (int)(rect[1] * shell.heightRatio), (int)(rect[2] * shell.widthRatio), (int)(rect[3] * shell.heightRatio));
		} else {
			searchRect = new Rect(0, 0, width, height);
		}
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int i = searchRect.top; i < searchRect.bottom; i++) {
			for (int j = searchRect.left; j < searchRect.right; j++) {
				int index = i * width + j;
				int pixel = pixels[index];
				outLoop: for (int[] color : colors) {
					if (compareColor(pixel, color[0], color[1], sim, CompareSim.ColorSim)) {
						innerLoop: for (int[][] offsetColor : offsetColors) {
							int index1 = (i + offsetColor[0][1]) * width + j + offsetColor[0][0];
							int pixel1 = pixels[index1];
							for (int k = 1; k < offsetColor.length; k++) {
								if (compareColor(pixel1, offsetColor[k][0], offsetColor[k][1], sim, CompareSim.ColorSim)) {
									continue innerLoop;
								}
							}
							continue outLoop;
						}
						return new Point(j, i);
					}
				}
			}
		}

		return null;
	}

	/**
	 * 多点比色
	 * 
	 * @param colorStrs
	 *            要比较的颜色点列表
	 * @param sim
	 *            相似度
	 * @return
	 */
	public static Boolean cmpColor(String[][] colorStrs, double sim) {
		return cmpColorBase(colorStrs, sim, true);
	}

	/**
	 * 多点比色, 不做截屏操作，使用上一次的截屏图片
	 * 
	 * @param colorStrs
	 *            要比较的颜色点列表
	 * @param sim
	 *            相似度
	 * @return
	 */
	public static Boolean cmpColorNotCap(String[][] colorStrs, double sim) {
		return cmpColorBase(colorStrs, sim, false);
	}

	/**
	 * 多点比色
	 * 
	 * @param colorStrs
	 *            要比较的颜色点列表
	 * @param sim
	 *            相似度
	 * @param isCap
	 *            是否截屏
	 * @return
	 */
	private static Boolean cmpColorBase(String[][] colorStrs, double sim, Boolean isCap) {
		// 解释颜色
		ArrayList<int[]> colors = new ArrayList<int[]>();
		for (String[] color : colorStrs) {
			if (color == null || color.length < 3)
				continue;
			int[] c = new int[4];
			c[0] = Integer.valueOf(color[0]); // x
			c[1] = Integer.valueOf(color[1]); // y
			c[2] = Integer.valueOf(color[2], 16); // color
			c[3] = color.length == 4 ? Integer.valueOf(color[3], 16) : 0; // offset color
			colors.add(c);
		}
		if (isCap)
			screenCap(screenshotPath);
		Bitmap bm = BitmapFactory.decodeFile(screenshotPath);
		if (bm != null) {
			RootShell shell = RootShell.getInstance();
			Iterator<int[]> iterator = colors.iterator();
			while (iterator.hasNext()) {
				int[] color = iterator.next();
				int colorB = bm.getPixel((int)(color[0] * shell.widthRatio), (int)(color[1] * shell.heightRatio));
				if (!compareColor(color[2], colorB, color[3], sim, CompareSim.ColorSim)) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	/**
	 * 查找文本
	 * 
	 * @param rect
	 *            查找区域范围
	 * @param textLib
	 *            要查找的文本的字库
	 * @param color
	 *            字体的颜色
	 * @param sim
	 *            相似度
	 * @param isFindAll
	 *            是否查找返回所有匹配文本
	 * @return 匹配文本对象列表
	 */
	public static List<TextBody> findText(int[] rect, String[] textLib, String[] colorStrs, double sim, boolean isFindAll) {
		if (textLib == null || textLib.length < 4 || colorStrs == null || colorStrs.length == 0)
			return null;
		if (sim <= 0)
			sim = 0.9;
		if (sim > 1.0)
			sim = 1.0;

		screenCap(screenshotPath);

		int[] color = new int[2];
		color[0] = Integer.valueOf(colorStrs[0], 16);
		if (colorStrs.length == 2)
			color[1] = Integer.valueOf(colorStrs[1], 16);

		Bitmap bitmap = ImageDigital.readImg(screenshotPath);
		if (bitmap == null)
			return null;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Rect searchRect = null;
		if (rect != null && rect.length == 4) {
			RootShell shell = RootShell.getInstance();
			searchRect = new Rect((int)(rect[0] * shell.widthRatio), (int)(rect[1] * shell.heightRatio), (int)(rect[2] * shell.widthRatio), (int)(rect[3] * shell.heightRatio));
		} else {
			searchRect = new Rect(0, 0, width, height);
		}
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		List<TextBody> textPoints = new ArrayList<TextBody>();
		String textHax = textLib[0]; // 文本点阵信息十六进制字符串
		int sum = Integer.valueOf(textLib[2]);
		int row = Integer.valueOf(textLib[3]); // 点阵有效行数
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < textHax.length(); i++) {
			String sub = textHax.substring(i, i + 1);
			result.append((binaryArray[Integer.valueOf(sub, 16)]));
		}
		String resultStr = result.toString();
		int column = resultStr.length() / row; // 点阵列数

		int offsetx = 0;
		for (int c = 0; c < column; c++) {
			char f = resultStr.charAt(c * row);
			if (f == '1') {
				offsetx = c;
				break;
			}
		}
		outLoop: for (int i = searchRect.top; i < searchRect.bottom; i++) {
			for (int j = searchRect.left; j < searchRect.right; j++) {
				int index = i * width + j;
				int pixel = pixels[index];

				if (compareColor(pixel, color[0], color[1], 0, null)) {
					int textX = j - offsetx;
					int textY = i;
					int matchNum = 0; // 匹配的点总数
					int otherMatchNum = 0; // 干扰匹配的点
					List<Point> points = new ArrayList<Point>();
					for (int r = 0; r < row; r++) {
						for (int c = 0; c < column; c++) {
							int textbx = textX + c;
							int textby = textY + r;
							int index1 = textby * width + textbx;
							int pixel1 = pixels[index1];
							char f = resultStr.charAt(r + c * row);
							if (f == '1') {
								if (compareColor(pixel1, color[0], color[1], 0, null)) {
									matchNum++;
								}
								points.add(new Point(textbx, textby));
							} else {
								if (compareColor(pixel1, color[0], color[1], 0, null)) {
									otherMatchNum++;
								}
							}
						}
					}

					double rate = (double) matchNum / sum;
					double otherRate = (double) otherMatchNum / (row * column - sum); // 干扰点比率
					if (rate > 0.5)
						System.out.println(matchNum + " : " + sum + " : " + rate + "  " + otherRate);
					if (rate >= sim && otherRate <= 1 - sim) {
						int pointX = textX + column / 2;
						int pointY = textY + row / 2;
						Point point = new Point(pointX, pointY);
						if (!listTextBodyContainsPoint(textPoints, point, 10)) {
							textPoints.add(new TextBody(0, matchNum, sum, sim, point, points));
						}
						if (!isFindAll)
							break outLoop;
					}
				}
			}
		}

		// if (isFindAll)
		// textPoints = filterMatchNum(textPoints);
		return textPoints;
	}

	/**
	 * 查找当前屏幕中指定的图片中心坐标
	 * 
	 * @param rect
	 *            查找范围
	 * @param picName
	 *            指定的图片名称
	 * @param offsetColor
	 *            偏色
	 * @param sim
	 *            相似度 取值范围：0~1.0
	 * @param isFindAll
	 *            是否查找所有图片坐标
	 * @return 图片坐标列表
	 */
	public static List<Point> findImage(int[] rect, String picName, String offsetColor, double sim, boolean isFindAll) {
		screenCap(screenshotPath);
		String searchPicPath = LuaApplication.getContextObject().getFilesDir() + File.separator + picName;
		Rect searchRect = null;
		if (rect != null && rect.length == 4) {
			RootShell shell = RootShell.getInstance();
			searchRect = new Rect((int)(rect[0] * shell.widthRatio), (int)(rect[1] * shell.heightRatio), (int)(rect[2] * shell.widthRatio), (int)(rect[3] * shell.heightRatio));
		}

		return findImage(searchPicPath, screenshotPath, searchRect, offsetColor, sim, isFindAll);
	}

	/**
	 * 查找图片，在指定父图里找到指定子图的坐标
	 * 
	 * @param subPic
	 *            子图路径
	 * @param parPic
	 *            父图路径
	 * @param searchRect
	 *            查找区域，如果为空，则查找整张父图
	 * @param offsetColor
	 *            偏色
	 * @param sim
	 *            相似度 取值范围：0~1.0
	 * @param isFindAll
	 *            是否查找所有相似的图片
	 * @return 返回查找到的图片的中心点坐标
	 */
	private static List<Point> findImage(String subPic, String parPic, Rect searchRect, String offsetColor, double sim, boolean isFindAll) {
		if (subPic == null || parPic == null || "".equals(subPic.trim()) || "".equals(parPic.trim())) {
			return null;
		}
		if (sim <= 0)
			sim = 0.9;
		if (sim > 1.0)
			sim = 1.0;

		int offset = 0;
		if (offsetColor != null && !"".equals(offsetColor.trim())) {
			offset = Integer.valueOf(offsetColor, 16);
		}

		List<Point> points = new ArrayList<Point>();
		Bitmap subBitmap = ImageDigital.readImg(subPic);
		Bitmap parBitmap = ImageDigital.readImg(parPic);
		if (subBitmap == null || parBitmap == null)
			return null;

		int subWidth = subBitmap.getWidth();
		int subHeight = subBitmap.getHeight();
		int parWidth = parBitmap.getWidth();
		int parHeight = parBitmap.getHeight();

		if (searchRect == null || searchRect.isEmpty()) {
			searchRect = new Rect(0, 0, parWidth, parHeight);
		}

		int startSubPixel = subBitmap.getPixel(0, 0);
		int[] subPixels = new int[subWidth * subHeight];
		subBitmap.getPixels(subPixels, 0, subWidth, 0, 0, subWidth, subHeight);
		int[] parPixels = new int[parWidth * parHeight];
		parBitmap.getPixels(parPixels, 0, parWidth, 0, 0, parWidth, parHeight);

		int smallOffsetX = 0, smallOffsetY = 0;
		int smallStartX = 0, smallStartY = 0;
		int pointX = -1, pointY = -1;

		outLoop: for (int i = searchRect.top; i < searchRect.bottom; i++) {
			for (int j = searchRect.left; j < searchRect.right; j++) {
				int x = j, y = i; // 父图x,坐标处的颜色值
				int parIndex = y * parWidth + x;
				int parPixel = parPixels[parIndex];

				if (compareColor(parPixel, startSubPixel, offset, sim, CompareSim.ColorAlikeDeg)) {
					smallStartX = x - smallOffsetX; // 待找的图X坐标
					smallStartY = y - smallOffsetY; // 待找的图Y坐标
					int sum = 0; // 所有需要对比的有效点
					int matchNum = 0; // 成功匹配的点

					for (int k = 0; k < subHeight; k++) {
						for (int k2 = 0; k2 < subWidth; k2++) {
							sum++;
							int x1 = k2, y1 = k;
							int subIndex = y1 * subWidth + x1;
							int subPixel = subPixels[subIndex];

							int x2 = x1 + smallStartX, y2 = y1 + smallStartY;
							int parReleativeIndex = y2 * parWidth + x2;
							int parReleativePixel = parPixels[parReleativeIndex];
							if (compareColor(subPixel, parReleativePixel, offset, sim, CompareSim.ColorAlikeDeg)) {
								matchNum++;
							}
						}
					}
					if ((double) matchNum / sum >= sim) {
						pointX = smallStartX + subWidth / 2;
						pointY = smallStartY + subHeight / 2;
						Point point = new Point(pointX, pointY);
						if (!listContainsPoint(points, point, 5)) {
							points.add(point);
						}
						if (!isFindAll)
							break outLoop;
					}
				}
			}
		}

		return points;
	}

	/**
	 * 截屏另存为
	 * 
	 * @param direcotry
	 *            另存为的目录
	 * @return 是否保存成功
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean saveScreImgToDir(String direcotry) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String fromPath = screenshotPath;
		String newName = format.format(new Date()) + ".png";
		String toPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			toPath = "/sdcard" + File.separator;
			fromPath = toPath + "screenshot.png";
		}
		if (direcotry != null && !direcotry.equals("")) {
			toPath += direcotry + File.separator;
			File file = new File(toPath);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		toPath += newName;
		return copyFile(fromPath, toPath);
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            要复制的文件路径
	 * @param newPath
	 *            复制到的路径
	 */
	private static boolean copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldFile = new File(oldPath);
			if (oldFile.exists()) {
				InputStream inputStream = new FileInputStream(oldFile);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inputStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inputStream.close();
				fs.close();
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 对比两个颜色
	 * 
	 * @param colorA
	 *            要对比的颜色
	 * @param colorB
	 *            拿来对比颜色
	 * @param offsetColorStr
	 *            对比偏色
	 * @param sim
	 *            相似度
	 * @param comSim
	 *            相似度比较算法
	 * @return 颜色是否相似
	 */
	private static Boolean compareColor(int colorA, int colorB, int offsetColor, double sim, CompareSim comSim) {
		int ra = Color.red(colorA);
		int ga = Color.green(colorA);
		int ba = Color.blue(colorA);
		int rb = Color.red(colorB);
		int gb = Color.green(colorB);
		int bb = Color.blue(colorB);

		Boolean isMatch = false;

		if (sim > 0 && sim < 1.0 && comSim != null) {
			isMatch = comSim.sim(ra, rb, ga, gb, ba, bb, sim);
		}

		// int offsetColor = new BigInteger("FF" + offsetColorStr,
		// 16).intValue(); 不对比alpha通道
		// Color.alpha(colorA) <= (Color.alpha(colorB) + Color
		// .alpha(offsetColor))
		// && Color.alpha(colorA) >= (Color.alpha(colorB) - Color
		// .alpha(offsetColor))
		if (!isMatch)
			isMatch = ra <= (rb + Color.red(offsetColor)) && ra >= (rb - Color.red(offsetColor)) && ga <= (gb + Color.green(offsetColor)) && ga >= (gb - Color.green(offsetColor))
					&& ba <= (bb + Color.blue(offsetColor)) && ba >= (bb - Color.blue(offsetColor));
		return isMatch;
	}

	/**
	 * 对比两个颜色
	 * 
	 * @param colorA
	 *            要对比的颜色
	 * @param colorB
	 *            拿来对比颜色
	 * @param offsetColorStr
	 *            对比偏色
	 * @param sim
	 *            相似度
	 * @param comSim
	 *            相似度比较算法
	 * @return 颜色是否相似
	 */
	public static Boolean compareColor(String colorA, String colorB, String offsetColor, double sim, int type) {
		if (colorA == null || colorB == null || "".equals(colorA.trim()) || "".equals(colorB.trim()))
			return false;
		int colorAInt = Integer.valueOf(colorA, 16);
		int colorBInt = Integer.valueOf(colorB, 16);
		int offsetColorInt = 0;
		if (offsetColor != null && !"".equals(offsetColor.trim()))
			offsetColorInt = Integer.valueOf(offsetColor, 16);
		CompareSim compareSim = null;
		if (type == 1) {
			compareSim = CompareSim.ColorAlikeDeg;
		} else if (type == 2) {
			compareSim = CompareSim.ColorSim;
		}
		return compareColor(colorAInt, colorBInt, offsetColorInt, sim, compareSim);
	}

	/**
	 * 判断点列表里是否已经包含指定的点
	 * 
	 * @param points
	 *            点列表
	 * @param point
	 *            指定点
	 * @param errorRange
	 *            容错范围
	 * @return 是否包含
	 */
	private static Boolean listContainsPoint(List<Point> points, Point point, double errorRange) {
		for (Point point2 : points) {
			if (point2.x <= point.x + errorRange && point2.x >= point.x - errorRange && point2.y <= point.y + errorRange && point2.y >= point.y - errorRange) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断文本点列表里是否已经包含指定的文本点
	 * 
	 * @param textPoints
	 *            文本点列表
	 * @param point
	 *            文本点
	 * @param errorRange
	 *            容错范围
	 * @return 是否包含
	 */
	private static Boolean listTextBodyContainsPoint(List<TextBody> textPoints, Point point, double errorRange) {
		for (TextBody textBody : textPoints) {
			if (textBody.getPoint().x <= point.x + errorRange && textBody.getPoint().x >= point.x - errorRange && textBody.getPoint().y <= point.y + errorRange
					&& textBody.getPoint().y >= point.y - errorRange) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 屏幕截取
	 * 
	 * @param screenshotPath
	 *            截屏图片保存路径
	 */
	private static void screenCap(String path) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			path = "/sdcard" + File.separator + "screenshot.png";
		}

		File file = new File(path);
		RootShell shell = RootShell.getInstance();
		shell.execCmd("rm " + path);
		for (int i = 0; i < maxWaitForGennerFileTime; i++) {
			if (file.exists()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}

		// su -c screencap -p
		shell.execCmd("/system/bin/screencap -p " + path);
		for (int i = 0; i < maxWaitForGennerFileTime; i++) {
			if (!(file.exists() && file.length() > 0)) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				return;
			}
		}
	}

	/**
	 * 过滤掉重叠的点
	 * 
	 * @param textPoints
	 *            找到的点列表
	 * @return
	 */
	@SuppressWarnings("unused")
	private static List<TextBody> filterMatchNum(List<TextBody> textPoints) {
		List<TextBody> results = new ArrayList<TextBody>();
		List<TextBody> removes = new ArrayList<TextBody>();

		for (int i = 0; i < textPoints.size(); i++) {
			TextBody textBodyA = textPoints.get(i);
			if (removes.contains(textBodyA))
				continue;
			for (int j = i; j < textPoints.size(); j++) {
				TextBody textBodyB = textPoints.get(j);
				if (removes.contains(textBodyB))
					continue;
				if (textBodyA != textBodyB) {
					int samePoint = 0;
					for (Point point : textBodyA.getPoints()) {
						if (textBodyB.getPoints().contains(point)) {
							samePoint++;
							break;
						}
					}
					if (samePoint > 0) { // 有1个以上的点重合，表示找到的两个图像重叠，删除像素点数少的图像
						if (textBodyA.getMatchNum() >= 1 || textBodyA.getMatchNum() >= textBodyB.getMatchNum()) {
							if (!results.contains(textBodyA)) {
								results.add(textBodyA);
							}
							if (!removes.contains(textBodyB)) {
								removes.add(textBodyB);
							}
						} else {
							if (!results.contains(textBodyB)) {
								results.add(textBodyB);
							}
							if (!removes.contains(textBodyA)) {
								removes.add(textBodyA);
							}
						}
					}
				}
			}
		}

		return results;
	}
}

/**
 * 颜色相似度的比较方法枚举
 * 
 * @author Joshua
 *
 */
enum CompareSim {
	ColorAlikeDeg(1), // 算出平均颜色的相似度
	ColorSim(2); // 直接比较颜色相似

	private int method;

	private CompareSim(int method) {
		this.method = method;
	}

	/**
	 * 比较两个颜色是否相似
	 * 
	 * @return 返回颜色是否相似
	 */
	public boolean sim(int ra, int rb, int ga, int gb, int ba, int bb, double sim) {
		if (method == 1)
			return 1 - (Math.abs(ra - rb) + Math.abs(ga - gb) + Math.abs(ba - bb)) / 3 / 255 >= sim;
		return Math.abs(ra - rb) < 255 * (1 - sim) && Math.abs(ga - gb) < 255 * (1 - sim) && Math.abs(ba - bb) < 255 * (1 - sim);
	}
}

/**
 * 查找的文本对象
 * 
 * @author Joshua
 *
 */
class TextBody {
	private int num; // 数字
	private int matchNum; // 匹配的像素点个数
	private int compareSum; // 总对比有效点个数
	private double sim; // 相似度
	private Point point; // 中心点坐标
	private List<Point> points; // 所有比对的像素点的坐标

	public TextBody(int num, int matchNum, int compareSum, double sim, Point point, List<Point> points) {
		this.num = num;
		this.matchNum = matchNum;
		this.compareSum = compareSum;
		this.sim = sim;
		this.point = point;
		this.points = points;
	}

	public int getNum() {
		return num;
	}

	public int getMatchNum() {
		return matchNum;
	}

	public int getCompareSum() {
		return compareSum;
	}

	public double getSim() {
		return sim;
	}

	public Point getPoint() {
		return point;
	}

	public List<Point> getPoints() {
		return points;
	}
}
