package com.androlua;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;
import com.luajava.LuaState;

/**
 * 自定义Application 用来管理全局状态信息， 比如context
 * 
 * @author Joshua
 * 
 */
public class LuaApplication extends Application implements LuaContext {

	private static LuaApplication luaApplication;
	protected SharedPreferences preferences;
	protected String localDir;
	protected String odexDir;
	protected String libDir;
	protected String luaMdDir;
	protected String luaCpath;
	protected String luaLpath;
	protected String luaExtDir;

	/**
	 * 返回单例
	 * 
	 * @return
	 */
	public static Context getContextObject() {
		return luaApplication;
	}

	public static LuaApplication getLuaApplication() {
		return luaApplication;
	}

	@Override
	public void regGc(LuaGcable obj) {
		// TODO: Implement this method
	}

	public int getWidth() {
		return getResources().getDisplayMetrics().widthPixels;
	}

	public int getHeight() {
		return getResources().getDisplayMetrics().heightPixels;
	}

	@Override
	public String getLuaDir(String dir) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String getLuaExtDir(String dir) {
		// TODO: Implement this method
		return null;
	}

	public String getLibDir() {
		return libDir;
	}

	public String getOdexDir() {
		return odexDir;
	}

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();

		// 定义文件夹
		localDir = getFilesDir().getAbsolutePath();
		odexDir = getDir("odex", Context.MODE_PRIVATE).getAbsolutePath();
		libDir = getDir("lib", Context.MODE_PRIVATE).getAbsolutePath();
		luaMdDir = getDir("lua", Context.MODE_PRIVATE).getAbsolutePath();
		luaCpath = getApplicationInfo().nativeLibraryDir + "/lib?.so" + ";"
				+ libDir + "/lib?.so";
		luaLpath = luaMdDir + "/?.lua;" + luaMdDir + "/lua/?.lua;" + luaMdDir
				+ "/?/init.lua;";

		luaApplication = this;

		preferences = getSharedPreferences("sp_setting",
				Context.MODE_WORLD_READABLE);
		preferences.edit().putBoolean("key_running_auto", false).commit();
		CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
		crashHandler.init(getApplicationContext());

		// 初始化AndroLua工作目录
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String sdDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			luaExtDir = sdDir + "/AndroLua";
		} else {
			File[] fs = new File("/storage").listFiles();
			for (File f : fs) {
				String[] ls = f.list();
				if (ls == null)
					continue;
				if (ls.length > 5)
					luaExtDir = f.getAbsolutePath() + "/AndroLua";
			}
			if (luaExtDir == null)
				luaExtDir = getDir("AndroLua", Context.MODE_PRIVATE)
						.getAbsolutePath();
		}

		File destDir = new File(luaExtDir);
		if (!destDir.exists())
			destDir.mkdirs();

		try {
			unApk("assets", localDir);
			unApk("lua", luaMdDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压Apk安装包
	 * 
	 * @param dir
	 *            要解压的包目录
	 * @param extDir
	 *            要解压复制到的目标目录
	 * @throws IOException
	 *             例外
	 */
	private void unApk(String dir, String extDir) throws IOException {
		int i = dir.length() + 1;
		ZipFile zip = new ZipFile(getApplicationInfo().publicSourceDir);
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String name = entry.getName();
			if (name.indexOf(dir) != 0)
				continue;
			String path = name.substring(i);
			if (entry.isDirectory()) {
				File f = new File(extDir + File.separator + path);
				if (!f.exists())
					f.mkdirs();
			} else {
				String fname = extDir + File.separator + path;
				File ff = new File(fname);
				File temp = new File(fname).getParentFile();
				if (!temp.exists()) {
					if (!temp.mkdirs()) {
						zip.close();
						throw new RuntimeException("create file "
								+ temp.getName() + " fail");
					}
				}
				if (ff.exists()
						&& entry.getSize() == ff.length()
						&& LuaUtil.getFileMD5(ff).equals(
								LuaUtil.getFileMD5(zip.getInputStream(entry))))
					continue;

				FileOutputStream out = new FileOutputStream(extDir
						+ File.separator + path);
				InputStream in = zip.getInputStream(entry);
				byte[] buf = new byte[2 ^ 16];
				int count = 0;
				while ((count = in.read(buf)) != -1) {
					out.write(buf, 0, count);
				}
				out.close();
				in.close();
			}
		}
		zip.close();
	}

	@Override
	public String getLuaDir() {
		return localDir;
	}

	static private HashMap<String, Object> data = new HashMap<String, Object>();

	@Override
	public void call(String name, Object... args) {
		// TODO: Implement this method
	}

	@Override
	public void set(String name, Object object) {
		data.put(name, object);
	}

	public Object get(String name) {
		return data.get(name);
	}

	public String getLocalDir() {
		return localDir;
	}

	public String getMdDir() {
		return luaMdDir;
	}

	@Override
	public String getLuaExtDir() {
		return luaExtDir;
	}

	@Override
	public String getLuaLpath() {
		return luaLpath;
	}

	@Override
	public String getLuaCpath() {
		return luaCpath;
	}

	@Override
	public Context getContext() {
		// TODO: Implement this method
		return this;
	}

	@Override
	public LuaState getLuaState() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Object doFile(String path, Object... arg) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public void sendMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}
