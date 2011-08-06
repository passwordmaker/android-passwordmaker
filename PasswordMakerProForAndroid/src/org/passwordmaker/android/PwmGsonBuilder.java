package org.passwordmaker.android;

import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;

import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;
import org.passwordmaker.android.PwmProfile.UrlComponents;

import com.tasermonkeys.google.json.Gson;
import com.tasermonkeys.google.json.GsonBuilder;
import com.tasermonkeys.google.json.JsonDeserializationContext;
import com.tasermonkeys.google.json.JsonDeserializer;
import com.tasermonkeys.google.json.JsonElement;
import com.tasermonkeys.google.json.JsonObject;
import com.tasermonkeys.google.json.JsonParseException;
import com.tasermonkeys.google.json.reflect.TypeToken;

public class PwmGsonBuilder {
	private static Gson serializer = makeBuilder().create();

	public static class PwmListSerializer implements JsonDeserializer<PwmProfileList> {
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

	public static class PwmProfileSerializer implements JsonDeserializer<PwmProfile> {
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
			if ( obj.has("currentPasswordHash") && obj.has("passwordSalt") && obj.has("storePasswordHash") && obj.get("storePasswordHash").getAsBoolean() )
				prof.setCurrentPasswordHash(obj.get("currentPasswordHash").getAsString(), obj.get("passwordSalt").getAsString());
			
			
			List<String> favs = serializer.fromJson(obj.get("pwmFavoriteInputs"), new TypeToken<List<String>>() {}.getType());
			prof.addFavorite(favs);
			return prof;
		}
	}

	
	public static GsonBuilder makeBuilder() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(PwmProfileList.class,
				new PwmListSerializer());
		builder.registerTypeAdapter(PwmProfile.class,
				new PwmProfileSerializer());
		return builder;
	}
}
