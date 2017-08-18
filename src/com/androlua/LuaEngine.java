package com.androlua;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

import com.donson.zhushoubase.BroadcastType;
import com.image.identification.ScreenAssistant;
import com.luajava.JavaFunction;
import com.luajava.LuaException;
import com.luajava.LuaState;
import com.luajava.LuaStateFactory;
import com.shell.RootShell;

import dalvik.system.DexClassLoader;

/**
 * 
 * @author Joshua
 *
 *         description Lua脚本解释工具
 *
 */
public class LuaEngine implements LuaBroadcastReceiver.OnReceiveListerer, LuaContext {

	private static LuaEngine instance = null;
	LuaState L; // Lua解释和执行由此对象完成
	private static Object mLock = new Object();
	private final StringBuilder output = new StringBuilder(); // 执行日志

	private static HashMap<String, LuaDexClassLoader> dexCache = new HashMap<String, LuaDexClassLoader>();

	public String luaCpath;
	private String localDir;
	private String odexDir;
	private String luaExtDir;
	private String luaLpath;
	public String luaDir;

	public String getLocalDir() {
		return localDir;
	}

	/**
	 * description 私有构造方法
	 */
	private LuaEngine() {
		// 定义文件夹
		LuaApplication app = LuaApplication.getLuaApplication();
		localDir = app.getLocalDir();
		odexDir = app.getOdexDir();
		luaCpath = app.getLuaCpath();
		luaDir = localDir;
		luaLpath = app.getLuaLpath();
		luaExtDir = app.getLuaExtDir();

		luaLpath = (luaDir + "/?.lua;" + luaDir + "/lua/?.lua;" + luaDir + "/?/init.lua;") + luaLpath;

		try {
			initLua();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * "" 初始化lua使用的Java函数
	 * 
	 * @throws Exception
	 */
	private void initLua() throws Exception {
		L = LuaStateFactory.newLuaState();
		L.openLibs();
		L.pushJavaObject(this);
		L.setGlobal("luaengine");
		L.pushObjectValue(ScreenAssistant.class);
		L.setGlobal("ScreenAssistant");
		L.pushObjectValue(Log.class); // 设置Lua执行环境里的可以调用的Java日志类
		L.setGlobal("Log");
		L.pushJavaObject(RootShell.getInstance());
		L.setGlobal("rootShell");
		L.pushJavaObject(LuaApplication.getContextObject());
		L.setGlobal("context");
		L.pushContext(LuaApplication.getContextObject());
		L.getGlobal("luajava");
		L.pushString(luaExtDir);
		L.setField(-2, "luaextdir");
		L.pushString(luaDir);
		L.setField(-2, "luadir");
		L.pop(1);

		JavaFunction print = new LuaPrint(this, L);
		print.register("print");

		resetPath();

		JavaFunction set = new JavaFunction(L) {
			@Override
			public int execute() throws LuaException {
				LuaThread thread = (LuaThread) L.toJavaObject(2);

				thread.set(L.toString(3), L.toJavaObject(4));
				return 0;
			}
		};
		set.register("set");

		JavaFunction call = new JavaFunction(L) {
			@Override
			public int execute() throws LuaException {
				LuaThread thread = (LuaThread) L.toJavaObject(2);

				int top = L.getTop();
				if (top > 3) {
					Object[] args = new Object[top - 3];
					for (int i = 4; i <= top; i++) {
						args[i - 4] = L.toJavaObject(i);
					}
					thread.call(L.toString(3), args);
				} else if (top == 3) {
					thread.call(L.toString(3));
				}

				return 0;
			};
		};
		call.register("call");

	}

	/**
	 * description 获取单例
	 * 
	 * @return Lua解释工具单例
	 */
	public static LuaEngine getInstance() {
		if (instance != null)
			return instance;

		synchronized (mLock) {
			instance = new LuaEngine();
			return instance;
		}
	}

	/**
	 * 重置path
	 */
	public void resetPath() {
		L.getGlobal("package");
		L.pushString(luaLpath);
		L.setField(-2, "path");
		L.pushString(luaCpath);
		L.setField(-2, "cpath");
		L.pop(1);
	}

	/**
	 * description 读取Lua脚本文件字符串内容
	 * 
	 * @param filePathString
	 *            要读取内容的脚本文件路径
	 * @return 读取出来的文件字符串内容
	 */
	public String readLuaFileContent(String filePathString) {
		BufferedReader br = null;
		try {
			File file = new File(filePathString);
			StringBuilder sb = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			String codeString = sb.toString();

			if (codeString != null && !codeString.trim().equals("")) {
				return codeString;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	/**
	 * 读取输入流的全部字节，并以String形式返回
	 * 
	 * @param is
	 * @return
	 */
	public String readStream(InputStream is) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int i = is.read();
			while (i != -1) {
				bo.write(i);
				i = is.read();
			}
			return bo.toString();
		} catch (IOException e) {
			Log.e("ReadStream", "读取文件流失败");
			return "";
		}
	}

	// **********************************执行脚本方法********************************************//

	/**
	 * description 安全的执行lua脚本语句
	 * 
	 * @param script
	 *            要执行的lua脚本语句
	 * @return 执行结果信息
	 */
	public String safeEvalLua(String script, Map<Object, Object> args) {
		String res = null;
		try {
			res = evalLua(script, args);
		} catch (LuaException e) {
			res = e.getMessage() + "\n";
		}
		return res;
	}

	/**
	 * description 执行lua语句，有可能抛出异常
	 * 
	 * @param script
	 *            要执行的lua脚本语句
	 * @return 执行结果信息
	 * @throws LuaException
	 *             当有错误发生时抛出的异常
	 */
	public String evalLua(String script, Map<Object, Object> args) throws LuaException {
		L.setTop(0);
		int ok = L.LloadString(script);
		if (ok == 0) {
			L.getGlobal("debug");
			L.getField(-1, "traceback");
			L.remove(-2);
			L.insert(-2);

			L.pushJavaObject(args);
			L.setGlobal("argss");

			ok = L.pcall(0, 0, -2);
			if (ok == 0) {
				String res = output.toString();
				output.setLength(0);
				return res;
			}
		}
		throw new LuaException(errorReason(ok) + ": " + L.toString(-1));
	}

	/**
	 * description 获取错误原因描述
	 * 
	 * @param error
	 *            错误原因代码
	 * @return 错误原因描述
	 */
	private String errorReason(int error) {
		switch (error) {
		case 4:
			return "Out of memory";
		case 3:
			return "Syntax error";
		case 2:
			return "Runtime error";
		case 1:
			return "Yield error";
		}
		return "Unknown error " + error;
	}

	/**
	 * description 执行Lua脚本命令
	 * 
	 * @param statemanet
	 *            要执行的脚本命令
	 * @param args
	 *            脚本执行需要的参数 MapHashMap<Object, Object> args = new HashMap<Object, Object>(); args.put("name", "joshua"); args.put("password", "123456"); args.put("url", "https://taobao.com");
	 * @return
	 */
	public String execLuaStatemanet(String statemanet, Map<Object, Object> args) {

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("require 'import'\n");
		stringBuffer.append("require 'common'\n");
		stringBuffer.append("local _fot_ = os.time()");
		stringBuffer.append("Log.i(\"LuaLog\", \"start exec lua script\")\n");
		stringBuffer.append("rootShell.simulateBroadcast(\"" + BroadcastType.ShellExecStart.getValue() + "\", \"\")\n");
		stringBuffer.append(statemanet + "\n");
		// String mainView = "local xxmain = \"com.donson.xxxiugaiqi/com.donson.myhook.MainActivity\"";
		// stringBuffer.append(mainView+"\n");
		// stringBuffer.append("rootShell.startActivity(\"-n \"..xxmain)\n");
		stringBuffer.append("rootShell.simulateBroadcast(\"" + BroadcastType.ShellExecFinish.getValue() + "\", \"\")\n");
		stringBuffer.append("local _lot_ = os.time()");
		stringBuffer.append("Log.i(\"LuaLog\", \"finish exec lua script time:\"..os.date(\"%M:%S\", _lot_ - _fot_))");

		String result = safeEvalLua(stringBuffer.toString(), args); // 加载Lua脚本字符串
		Log.i("LuaEvalResult", "result is : " + result);
		return result;
	}

	/**
	 * description 执行Lua脚本文件
	 * 
	 * @param filePathString
	 *            要执行的Lua脚本文件路径
	 * @param args
	 *            脚本执行需要的参数Map
	 * @return
	 */
	public String execLuaScriptFile(String filePathString, Map<Object, Object> args) {
		String statemanet = readLuaFileContent(filePathString);
		if ("".equals(statemanet)) {
			String error = "lua script file is empty!";
			System.out.println(error);
			return error;
		}

		return execLuaStatemanet(statemanet, args);
	}

	public DexClassLoader loadDex(String path) throws LuaException {
		LuaDexClassLoader dex = dexCache.get(path);
		if (dex != null)
			return dex;

		if (path.charAt(0) != '/')
			path = luaDir + "/" + path;
		if (!new File(path).exists())
			if (new File(path + ".dex").exists())
				path += ".dex";
			else if (new File(path + ".jar").exists())
				path += ".jar";
			else
				throw new LuaException(path + " not found");
		if (path.endsWith(".jar"))
			loadResources(path);
		dex = new LuaDexClassLoader(path, odexDir, LuaApplication.getContextObject().getApplicationInfo().nativeLibraryDir, LuaApplication.getContextObject().getClassLoader());
		dexCache.put(path, dex);
		return dex;
	}

	private static class LuaDexClassLoader extends DexClassLoader {
		private static HashMap<String, Class<?>> classCache = new HashMap<String, Class<?>>();

		public LuaDexClassLoader(java.lang.String dexPath, java.lang.String optimizedDirectory, java.lang.String libraryPath, java.lang.ClassLoader parent) {
			super(dexPath, optimizedDirectory, libraryPath, parent);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			// TODO: Implement this method
			Class<?> cls = classCache.get(name);
			if (cls == null) {
				cls = super.findClass(name);
				classCache.put(name, cls);
			}
			return cls;
		}
	}

	public void loadResources(String path) {
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
			addAssetPath.invoke(assetManager, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void call(String func, Object... args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set(String name, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLuaDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLuaDir(String dir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLuaExtDir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLuaExtDir(String dir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLuaLpath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLuaCpath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LuaState getLuaState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object doFile(String path, Object... arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMsg(String msg) {
		Log.d("lua", msg);
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void regGc(LuaGcable obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

	}
}
