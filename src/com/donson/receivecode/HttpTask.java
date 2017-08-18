package com.donson.receivecode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.AsyncTask;

import com.androlua.LuaUtil;

public class HttpTask extends AsyncTask<Object, Object, Object> {

	private String mUrl;

	private HttpCallBackListener mListener;

	private byte[] mData;

	private String mCharset;

	private String mCookie;

	private HashMap<String, String> mHeader;

	private String mMethod;

	public HttpTask(String url, String method, String cookie, String charset, HashMap<String, String> header, HttpCallBackListener listener) {
		mUrl = url;
		mMethod = method;
		mCookie = cookie;
		mCharset = charset;
		mHeader = header;
		mListener = listener;
	}

	@Override
	protected Object doInBackground(Object... p1) {
		try {
			URL url = new URL(getmUrl());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6000);
			HttpURLConnection.setFollowRedirects(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");

			if (mCharset == null)
				mCharset = "UTF-8";
			conn.setRequestProperty("Accept-Charset", mCharset);

			if (mCookie != null)
				conn.setRequestProperty("Cookie", mCookie);

			if (mHeader != null) {
				Set<Map.Entry<String, String>> entries = mHeader.entrySet();
				for (Map.Entry<String, String> entry : entries) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			if (mMethod != null)
				conn.setRequestMethod(mMethod);

			if (!"GET".equals(mMethod) && p1.length != 0) {
				mData = formatData(p1);

				conn.setDoOutput(true);
				conn.setRequestProperty("Content-length", "" + mData.length);
			}

			conn.connect();

			// download
			if ("GET".equals(mMethod) && p1.length != 0) {
				File f = new File((String) p1[0]);
				if (!f.getParentFile().exists())
					f.getParentFile().mkdirs();
				FileOutputStream os = new FileOutputStream(f);
				InputStream is = conn.getInputStream();
				LuaUtil.copyFile(is, os);
				return new Object[] { conn.getResponseCode(), conn.getHeaderFields() };
			}

			// post upload
			if (p1.length != 0) {
				OutputStream os = conn.getOutputStream();
				os.write(mData);
			}

			int code = conn.getResponseCode();
			Map<String, List<String>> hs = conn.getHeaderFields();
			if (code >= 200 && code < 400) {
				List<String> cs = hs.get("Set-Cookie");
				StringBuffer cok = new StringBuffer();
				if (cs != null)
					for (String s : cs) {
						cok.append(s + ";");
					}

				InputStream is = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, mCharset));
				StringBuffer buf = new StringBuffer();
				String line;
				while ((line = reader.readLine()) != null && !isCancelled())
					buf.append(line + '\n');
				is.close();
				return new Object[] { code, new String(buf), cok.toString(), hs };
			} else {
				return new Object[] { code, conn.getResponseMessage(), null, hs };
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Object[] { -1, e.getMessage() };
		}
	}

	@SuppressWarnings("unchecked")
	private byte[] formatData(Object[] p1) throws UnsupportedEncodingException, IOException {
		byte[] bs = null;
		if (p1.length == 1) {
			Object obj = p1[0];
			if (obj instanceof String)
				bs = ((String) obj).getBytes(mCharset);
			else if (obj.getClass().getComponentType() == byte.class)
				bs = (byte[]) obj;
			else if (obj instanceof File)
				bs = LuaUtil.readAll(new FileInputStream((File) obj));
			else if (obj instanceof Map<?, ?>)
				bs = formatData((Map<Object, Object>) obj);
		}
		return bs;
	}

	private byte[] formatData(Map<Object, Object> obj) throws UnsupportedEncodingException {
		StringBuilder buf = new StringBuilder();
		Set<Map.Entry<String, String>> entries = mHeader.entrySet();
		for (Map.Entry<String, String> entry : entries) {
			buf.append(entry.getKey() + "=" + entry.getValue() + "&");
		}
		return buf.toString().getBytes(mCharset);
	}

	public boolean cancel() {
		return super.cancel(true);
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		if (isCancelled())
			return;
		try {
			if (mListener != null) {
				mListener.onSuccess(result);
			}
		} catch (Exception e) {
			if (mListener != null) {
				mListener.onEror(e);
			}
		}
	}

	public String getmUrl() {
		return mUrl;
	}
}