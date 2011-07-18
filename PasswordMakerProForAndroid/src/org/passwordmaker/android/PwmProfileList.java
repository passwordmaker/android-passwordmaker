package org.passwordmaker.android;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PwmProfileList implements Map<String, PwmProfile> {

	Map<String, PwmProfile> profiles = new HashMap<String, PwmProfile>();
	
	public boolean set(PwmProfile profile) {
		profiles.put(profile.getName(), profile);
		return true;
	}

	public String[] toArray() {
		return toArray(new String[0]);
	}

	public <T> T[] toArray(T[] array) {
		return profiles.values().toArray(array);
	}

	public String[] toProfileNames() {
		Set<String> vals = profiles.keySet();
		return vals.toArray(new String[0]);
	}

	public void add(String name) {
		set(new PwmProfile(name));
		
	}

	public void clear() {
		profiles.clear();
		
	}

	public boolean containsKey(Object key) {
		return profiles.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return profiles.containsValue(value);
	}

	public Set<Map.Entry<String, PwmProfile>> entrySet() {
		return profiles.entrySet();
	}

	public PwmProfile get(Object key) {
		return profiles.get(key);
	}

	public boolean isEmpty() {
		return profiles.isEmpty();
	}

	public Set<String> keySet() {
		return profiles.keySet();
	}

	public PwmProfile put(String key, PwmProfile value) {
		return profiles.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends PwmProfile> objects) {
		putAll(objects.values());
	}

	public void putAll(Collection<? extends PwmProfile> objects) {
		for ( PwmProfile profile : objects ) {
			set(profile);
		}
	}
	
	public PwmProfile remove(Object key) {
		return profiles.remove(key);
	}

	public int size() {
		return profiles.size();
	}

	public Collection<PwmProfile> values() {
		return profiles.values();
	}
	
}
