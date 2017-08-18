package com.shell;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.androlua.LuaApplication;
import com.image.identification.Identification;

/**
 * 用root权限执行Linux下的shell指令
 *
 * @author Joshua
 * 
 */
public class RootShell {
	private static RootShell instance = null;
	private static Object mLock = new Object();
	private String mShell;

	private OutputStream oStream;
	private Process process;
	private int excTimes; // 当前执行多少条adb shell指令

	public float widthRatio; // 宽度比率
	public float heightRatio; // 高度比率

	/**
	 * 私有构造方法
	 * 
	 * @param shell
	 *            设备shell的类型
	 */
	private RootShell(String shell) throws Exception {
		this.mShell = shell;
		init();
	}

	/**
	 * 初始化方法
	 * 
	 * @throws Exception
	 */
	private void init() throws Exception {
		if (this.process != null && this.oStream != null) {
			this.oStream.flush();
			this.oStream.close();
			this.process.destroy();
		}

		this.process = Runtime.getRuntime().exec(this.mShell);
		this.oStream = this.process.getOutputStream();
		execCmd("LD_LIBRARY_PATH=/vendor/lib:/system/lib");

		DisplayMetrics dm = LuaApplication.getContextObject().getResources()
				.getDisplayMetrics();
		widthRatio = dm.widthPixels / 720.f;
		heightRatio = dm.heightPixels / 1280.f;
	}

	/**
	 * 单例方法
	 * 
	 * @return RootShell单例
	 */
	public static RootShell getInstance() {
		if (instance != null) {
			return instance;
		}

		synchronized (mLock) {
			try {
				instance = new RootShell("su");
			} catch (Exception e) {
				try {
					instance = new RootShell("/system/xbin/su");
				} catch (Exception e2) {
					try {
						instance = new RootShell("/system/bin/su");
					} catch (Exception e3) {
						e3.printStackTrace();
					}
				}
			}
			return instance;
		}
	}

	/**
	 * 执行shell指令, 确保应用没退出，不然命令可能不执行
	 * 
	 * @param cmdString
	 *            要执行的指令
	 */
	public final void execCmd(String cmdString) {
		Log.i("RootShell","cmdString:"+cmdString);
		try {
			this.excTimes++;
			if (this.excTimes > 40) {
				this.excTimes = 0;
				init();
			}
			this.oStream.write((cmdString + "\n").getBytes("ASCII"));
			this.oStream.flush();
			return;
		} catch (Exception e) {
			System.out.println("!!!!!!!++++++++" + e.getMessage());
			while (true) {
				try {
					init();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	// ************************基本操作************************//

	/**
	 * 重启设备
	 */
	public final void reboot() {
		execCmd("adb reboot");
	}

	/**
	 * 安装apk
	 * 
	 * @param apkPath
	 */
	public final void installApk(String apkPath) {
		execCmd("pm install -r " + apkPath);
	}

	/**
	 * 卸载app
	 * 
	 * @param packageName
	 *            要卸载的app包名
	 */
	public final void uninstallApk(String packageName) {
		execCmd("pm uninstall " + packageName);
	}

	/**
	 * 清除app的数据
	 * 
	 * @param packageName
	 */
	public final void clearPackage(String packageName) {
		execCmd("pm clear " + packageName);
	}

	/**
	 * 启动某个应用的某个界面
	 * 
	 * @param activityName
	 *            界面名称
	 */
	public final void startActivity(String activityName) {
		execCmd("am start " + activityName);
	}
	
	/**
	 * 结束指定包名的进程
	 * 
	 * @param packageName 进程的包名
	 */
	public final void stopProgress(String packageName) {
		execCmd("am force-stop " + packageName);
	}
	/**
	 * 后台模拟全局按键
	 * 
	 * @param keyCode
	 *            键值
	 */
	public final void simulateKey(int keyCode) {
		execCmd("input keyevent " + keyCode);
	}

	/**
	 * 模拟屏幕点击
	 * 
	 * @param x
	 *            点击的点的横坐标
	 * @param y
	 *            点击的点的纵坐标
	 */
	public final void simulateTap(int x, int y) {
		execCmd("input tap " + x * widthRatio + " " + y * heightRatio);
	}

	/**
	 * 模拟滑动操作
	 * 
	 * @param startX
	 *            滑动起点横坐标
	 * @param startY
	 *            滑动起点纵坐标
	 * @param endX
	 *            滑动终点横坐标
	 * @param endY
	 *            滑动终点纵坐标
	 * @param delay
	 *            滑动延继时间
	 */
	public final void simulateSwipe(int startX, int startY, int endX, int endY,
			int delay) {
		String str = delay == 0 ? "" : " " + delay;
		execCmd("input touchscreen swipe " + startX * widthRatio + " " + startY
				* heightRatio + " " + endX * widthRatio + " " + endY
				* heightRatio + str);
	}

	/**
	 * adb sleep
	 * 
	 * @param time
	 *            时间
	 */
	public final void sleep(int time) {
		execCmd("sleep " + time);
	}

	/**
	 * 模拟输入文字
	 * 
	 * @param string
	 *            要输入的文字
	 */
	public final void simulateTypeText(String string) {
		char[] chars = string.toCharArray();
		StringBuilder sBuilder = new StringBuilder();
		for (char d : chars) {
			sBuilder.append((int) d);
			sBuilder.append(',');
		}
		simulateBroadcast("ADB_INPUT_CHARS", "--eia chars '" + sBuilder.toString() + "'");
	}

	/**
	 * 模拟发送广播
	 * 
	 * @param name
	 *            Action
	 * @param datas
	 *            携带的数据 ps: --es key "value" \ --ei key 33 \ --ez key false
	 */
	public final void simulateBroadcast(String name, String datas) {
		execCmd("am broadcast -a " + name + " " + datas);
	}

	/**
	 * 获取手机系统的SDK版本号
	 * @return
	 */
	public final int getSystemSDKVersion() {
		return Build.VERSION.SDK_INT;
	}
	
	/**
	 * 获取当前界面的名称
	 * 
	 * @return 界面名称
	 */
	public final String getCurrentActivityName() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Log.i("RootShell","CUR1:"+getActivityNameNew(1));
			return getActivityNameNew(1);
		} else {
			ActivityManager activityManager = (ActivityManager) LuaApplication
					.getContextObject().getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			@SuppressWarnings("deprecation")
			List<ActivityManager.RunningTaskInfo> taskInfo = activityManager
					.getRunningTasks(1);
			Log.i("RootShell","CUR:"+taskInfo.get(0).topActivity.toString());
			return taskInfo.get(0).topActivity.toString();
		}
	}
	
	/**
	 * 获取上一个界面名称
	 * @return 界面名称
	 */
	public final String getBeforeActivityName() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return getActivityNameNew(2);
		} else {
			return "";
		}
	}

	/**
	 * 高版本获取当前ActivityName 方法
	 * 
	 * @return
	 */

	@TargetApi(22)
	private String getActivityNameNew(int type) {
		UsageStatsManager usageStatsManager = (UsageStatsManager) LuaApplication
				.getContextObject().getSystemService(
						Context.USAGE_STATS_SERVICE);
		long time = System.currentTimeMillis();
		String result = "";
		UsageEvents.Event event = new UsageEvents.Event();
		UsageEvents usageEvents = usageStatsManager.queryEvents(time - 1000 * 60, time);
		while (usageEvents.hasNextEvent()) {
			usageEvents.getNextEvent(event);
			if (event.getEventType() == type) {
				ComponentName com = new ComponentName(event.getPackageName(), event.getClassName());
				result = com.toString();
			}
		}
		
		if (!android.text.TextUtils.isEmpty(result)) {
			return result;
		}
		return null;
	}

	/**
	 * 获取当前运行的应用包
	 * 
	 * @return
	 */
	public String[] getActivePackages() {
		final Set<String> activePackages = new HashSet<String>();
		ActivityManager activityManager = (ActivityManager) LuaApplication
				.getContextObject().getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		final List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : processInfos) {
			if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				activePackages.addAll(Arrays
						.asList(runningAppProcessInfo.pkgList));
			}
		}
		return activePackages.toArray(new String[activePackages.size()]);
	}

	/**
	 * 获取已经安装的应用列表
	 * 
	 * @return
	 */
	public String[] getInstalledPackages() {
		final Set<String> installedPackages = new HashSet<String>();
		PackageManager packageManager = LuaApplication.getContextObject()
				.getPackageManager();
		List<ApplicationInfo> infos = packageManager
				.getInstalledApplications(0);
		for (ApplicationInfo applicationInfo : infos) {
			installedPackages.add("app : "
					+ applicationInfo.packageName
					+ " "
					+ packageManager.getApplicationLabel(applicationInfo)
							.toString());
		}
		return installedPackages.toArray(new String[installedPackages.size()]);
	}
	
	/**
	 * 获取网络类型
	 * 
	 * @return
	 */
	public int getConnectedType() {
		Context context = LuaApplication.getContextObject();
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable())
				return mNetworkInfo.getType();
		}
		return -1;
	}

	/**
	 * 获取宽度比率
	 * 
	 * @return 宽度比率
	 */
	public float getWidthRatio() {
		return widthRatio;
	}

	/**
	 * 获取高度比率
	 * 
	 * @return 高度比率
	 */
	public float getHeightRatio() {
		return heightRatio;
	}

	/**
	 * 对比图片，求图片的相似度
	 * 
	 * @param str1
	 *            原图片的地址
	 * @param str2
	 *            要比较图片的地址
	 * @param idents
	 *            Identification接口的实例化对像，可以传入多个对像
	 * @return 图片相似度
	 */
	public float compareImage(String pic1, String pic2,
			Identification... idents) {
		int characNum = idents.length;
		String charact1[] = new String[characNum];
		String charact2[] = new String[characNum];
		// 获得str1中图片的特征值
		for (int i = 0; i < characNum; i++) {
			charact1[i] = idents[i].getCharacteristic(pic1);
		}

		float f = 0f;
		for (int i = 0; i < characNum; i++) {
			charact2[i] = idents[i].getCharacteristic(pic2);
			f += idents[i].identification(charact1[i], charact2[i]);
		}
		return (int) ((f / characNum) * 1000) / 1000f;
	}

	// *******************************中文处理******************************** //

	/**
	 * 把中文转成Unicode码
	 * 
	 * @param string
	 *            待转换中文字符串
	 * @return Unicode码字符串
	 */
	public String chineseToUnicode(String string) {
		String result = "";
		for (int i = 0; i < string.length(); i++) {
			int chr = (char) string.charAt(i);
			if (chr >= 19968 && chr <= 171941) {
				result += "\\u" + Integer.toHexString(chr);
			} else {
				result += string.charAt(i);
			}
		}
		return result;
	}

	/**
	 * 判断是否为中文字符
	 * 
	 * @param c
	 *            字符
	 * @return
	 */
	public boolean isChinese(char c) {
		Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);
		if (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| unicodeBlock == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| unicodeBlock == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| unicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	// *******************************执行adb脚本******************************** //

	/**
	 * 执行adb shell命令脚本
	 * 
	 * @param filePathString
	 *            命令脚本文件路径
	 * @return 是否成功执行脚本
	 */
	public final boolean execCmdFromFile(String filePathString) {
		try {
			Vector<String> vector = readFile(filePathString);

			if (vector != null) {
				for (String cmdString : vector) {
					execCmd(cmdString);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 读取命令脚本文件
	 * 
	 * @param filePathString
	 *            命令脚本文件路径
	 * @return 命令集合
	 * @throws IOException
	 *             读取文件异常
	 */
	private Vector<String> readFile(String filePathString) throws IOException {
		Vector<String> vector = new Vector<String>();

		// 文件缓冲输入流
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePathString)));

		// 读取数据
		String cmdString = bufferedReader.readLine();
		while (cmdString != null) {
			vector.add(cmdString);
			cmdString = bufferedReader.readLine();
		}

		bufferedReader.close();

		return vector;
	}

	// *******************************root权限******************************** //

	/**
	 * 判断是否已经root了
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean hasRootAccess(Context ctx) {
		final StringBuilder res = new StringBuilder();
		try {
			if (runCommandAsRoot(ctx, "exit 0", res) == 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 以root权限执行命令
	 * 
	 * @param ctx
	 * @param script
	 * @param res
	 * @return
	 */
	public static int runCommandAsRoot(Context ctx, String script,
			StringBuilder res) {
		final File file = new File(ctx.getCacheDir(), "secopt.sh");
		final ScriptRunner runner = new ScriptRunner(file, script, res);
		runner.start();

		try {
			runner.join(40000);
			if (runner.isAlive()) {
				runner.interrupt();
				runner.join(150);
				runner.destroy();
				runner.join(50);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return runner.exitcode;
	}

	private static final class ScriptRunner extends Thread {
		private final File file;
		private final String script;
		private final StringBuilder res;
		public int exitcode = -1;
		private Process exec;

		public ScriptRunner(File file, String script, StringBuilder res) {
			this.file = file;
			this.script = script;
			this.res = res;
		}

		@Override
		public void run() {
			try {
				file.createNewFile();
				final String abspath = file.getAbsolutePath();
				Runtime.getRuntime().exec("chmod 777 " + abspath).waitFor();
				final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
						new FileOutputStream(file));
				if (new File("/system/bin/sh").exists()) {
					outputStreamWriter.write("#!/system/bin/sh\n");
				}
				outputStreamWriter.write(script);
				if (!script.endsWith("\n")) {
					outputStreamWriter.write("\n");
				}
				outputStreamWriter.write("exit\n");
				outputStreamWriter.flush();
				outputStreamWriter.close();

				exec = Runtime.getRuntime().exec("su");
				DataOutputStream oStream = new DataOutputStream(
						exec.getOutputStream());
				oStream.writeBytes(abspath);
				oStream.flush();
				oStream.close();

				InputStreamReader reader = new InputStreamReader(
						exec.getInputStream());
				final char buf[] = new char[1024];
				int read = 0;
				while ((read = reader.read(buf)) != -1) {
					if (res != null) {
						res.append(buf, 0, read);
					}
				}

				reader = new InputStreamReader(exec.getErrorStream());
				read = 0;
				while ((read = reader.read(buf)) != -1) {
					if (res != null) {
						res.append(buf, 0, read);
					}
				}

				if (exec != null) {
					this.exitcode = exec.waitFor();
				}
			} catch (InterruptedException ex) {
				if (res != null) {
					res.append("\nOperation timed-out");
				}
			} catch (Exception ex) {
				if (res != null) {
					res.append("\n" + ex);
				}
			} finally {
				destroy();
			}
		}

		public synchronized void destroy() {
			if (exec != null) {
				exec.destroy();
			}
			exec = null;
		}
	}
}
