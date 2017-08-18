package com.donson.zhushoubase;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.util.Log;

import com.androlua.LuaApplication;
import com.androlua.LuaEngine;

public class Test extends AndroidTestCase {

	public Test() {

	}

	// **************测试脚本****************//
	/**
	 * 测试执行lua脚本语句
	 */
	public void testExecLuaScript() {
		String statemanet = LuaEngine.getInstance().readStream(
				LuaApplication.getContextObject().getResources()
						.openRawResource(R.raw.hello));

		// 参数
		HashMap<Object, Object> args = new HashMap<Object, Object>();
		args.put("wifistate","0");
		args.put("args1", 2222);
		args.put("args2", "haohao");
		args.put("args3", "gggdef");

		LuaEngine.getInstance().execLuaStatemanet(statemanet, args);
		
	}

	/**
	 * 测试执行Lua脚本文件
	 */
	public void testExecLuaScriptFile() {
		String filePath = LuaApplication.getContextObject().getFilesDir() + "/"
				+ "hello.lua";
		LuaEngine.getInstance().execLuaScriptFile(filePath, null);
	}

	// **************本应用文件操作****************//
	/**
	 * 将/res/raw下面的资源复制到 /data/data/applicaton.package.name/files
	 */
	public void copyResourcesToLocal() {
		String name, sFileName;
		InputStream content;
		R.raw a = new R.raw();
		java.lang.reflect.Field[] t = R.raw.class.getFields();
		Resources resources = LuaApplication.getContextObject().getResources();
		for (int i = 0; i < t.length; i++) {
			FileOutputStream fs = null;
			try {
				name = resources.getText(t[i].getInt(a)).toString();
				sFileName = name.substring(name.lastIndexOf('/') + 1,
						name.length());
				content = resources.openRawResource(t[i].getInt(a));

				// Copies script to internal memory only if changes were made
				sFileName = LuaApplication.getContextObject().getFilesDir()
						+ "/" + sFileName;

				Log.d("Copy Raw File", "Copying from stream " + sFileName);
				content.reset();
				int bytesum = 0;
				int byteread = 0;
				fs = new FileOutputStream(sFileName);
				byte[] buffer = new byte[1024];
				while ((byteread = content.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				fs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
