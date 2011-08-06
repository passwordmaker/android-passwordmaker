package org.passwordmaker.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.content.Context;
import android.util.Log;

import com.tasermonkeys.google.json.Gson;

public class PrivateSettingsStorage {

	private static PrivateSettingsStorage instance = new PrivateSettingsStorage();
	private Gson serializer;
	
	private PrivateSettingsStorage() {
		serializer = PwmGsonBuilder.makeBuilder().create();
	}

	public static PrivateSettingsStorage getInstance() {
		return instance;
	}

	public void putObject(Context context, String key, Object obj)
			throws IOException {
		FileOutputStream fos = context.openFileOutput(key + ".pss",
				Context.MODE_PRIVATE);
		try {
			String jsonStr = serializer.toJson(obj);
			Log.d("PrivateSettingsStorage", "JsonData-Store: " + jsonStr);
			fos.write(jsonStr.getBytes("UTF-8"));
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) {/* Suppress errors from closing */
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Context context, String key, T defaultValue)
			throws IOException {
		String filename = key + ".pss";
		File f = new File(context.getFilesDir(), filename);
		if (!f.exists())
			return defaultValue;
		InputStream fis = context.openFileInput(filename);
		String fool = IOUtils.toString(fis);
		fis = IOUtils.toInputStream(fool);
		Log.d("PrivateSettingsStorage", "JsonData-Get: " + fool);
		try {
			Reader reader = new InputStreamReader(fis, "UTF-8");
			return (T) serializer.fromJson(reader, defaultValue.getClass());
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Exception e) {/* Suppress errors from closing */
			}
		}
	}

}
