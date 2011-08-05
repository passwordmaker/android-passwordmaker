package org.passwordmaker.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;

import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;
import org.passwordmaker.android.PwmProfile.UrlComponents;

import android.content.Context;

import com.tasermonkeys.google.json.*;
import com.tasermonkeys.google.json.reflect.*;

public class PrivateSettingsStorage {

	private static PrivateSettingsStorage instance = new PrivateSettingsStorage();
	private Gson serializer;

	public class PwmListSerializer implements JsonDeserializer<PwmProfileList> {
		public PwmProfileList deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			PwmProfileList pwmList = new PwmProfileList();
			for (Entry<String, JsonElement> x : obj.entrySet()) {
				PwmProfile profile = serializer.fromJson(x.getValue(),
						PwmProfile.class);
				pwmList.set(profile);
			}
			return pwmList;
		}
	}

	// 07-31 17:58:22.496: INFO/System.out(791):
	// {"characters":"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
	// "currentAlgo":"MD5","leetLevel":"One","username":"","modifier":"","name":"Default","passwordPrefix":"",
	// "passwordSuffix":"","pwmFavoriteInputs":[],"urlComponents":["Domain"],"useLeet":"NotAtAll",
	// "lengthOfPassword":8}

	public class PwmProfileSerializer implements JsonDeserializer<PwmProfile> {
		public PwmProfile deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {

			JsonObject obj = json.getAsJsonObject();

			PwmProfile prof = new PwmProfile(obj.get("name").getAsString());
			prof.setCharacters(obj.get("characters").getAsString());
			prof.setHashAlgo(HashAlgo.valueOf(obj.get("currentAlgo")
					.getAsString()));
			prof.setLeetLevel(LeetLevel.valueOf(obj.get("leetLevel")
					.getAsString()));
			prof.setUsername(obj.get("username").getAsString());
			prof.setModifier(obj.get("modifier").getAsString());
			prof.setPrefix(obj.get("passwordPrefix").getAsString());
			prof.setSuffix(obj.get("passwordSuffix").getAsString());
			List<String> urlCompondents = serializer.fromJson(obj.get("urlComponents"), new TypeToken<List<String>>() {}.getType());
			EnumSet<UrlComponents> esUrls = EnumSet.noneOf(UrlComponents.class);
			for (String urlComp : urlCompondents) {
				esUrls.add(UrlComponents.valueOf(urlComp));
			}
			prof.setUrlComponents(esUrls);
			prof.setUseLeet(UseLeet.valueOf(obj.get("useLeet")
					.getAsString()));
			prof.setLeetLevel(LeetLevel.valueOf(obj.get("leetLevel")
					.getAsString()));
			prof.setLengthOfPassword(obj.get("lengthOfPassword").getAsShort());
			List<String> favs = serializer.fromJson(obj.get("pwmFavoriteInputs"), new TypeToken<List<String>>() {}.getType());
			prof.addFavorite(favs);
			return prof;
		}
	}

	private PrivateSettingsStorage() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(PwmProfileList.class,
				new PwmListSerializer());
		builder.registerTypeAdapter(PwmProfile.class,
				new PwmProfileSerializer());
		serializer = builder.create();
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
		FileInputStream fis = context.openFileInput(filename);
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
