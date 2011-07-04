package org.passwordmaker.android;

import java.util.EnumSet;

import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;

public class PwmProfile {

	
	public enum UrlComponents {
		Protocol, Subdomain, Domain, PortPathAnchorQuery
	}
	
	private PwmHashAlgorithm hashAlgo = PwmHashAlgorithm.get(HashAlgo.MD5);
	private UseLeet useLeet = UseLeet.NotAtAll;
	private LeetLevel leetLevel = LeetLevel.One;
	private EnumSet<UrlComponents> urlComponents = defaultUrlComponents();
	private short lengthOfPassword = 8;
	private String username = "";
	private String modifier = "";
	private String characters = CharacterSetSelection.alphaNum.getCharacterSet();
	private String passwordPrefix = "";
	private String passwordSuffix = "";
	
	public PwmProfile() {
	}

	public static EnumSet<UrlComponents> defaultUrlComponents() {
		return EnumSet.of(UrlComponents.Domain);
	}
	
	public PwmHashAlgorithm getHashAlgo() {
		return hashAlgo;
	}

	public void setHashAlgo(PwmHashAlgorithm hashAlgo) {
		this.hashAlgo = hashAlgo;
	}
	
	public UseLeet getUseLeet() {
		return useLeet;
	}

	public void setUseLeet(UseLeet useLeet) {
		this.useLeet = useLeet;
	}

	public LeetLevel getLeetLevel() {
		return leetLevel;
	}

	public void setLeetLevel(LeetLevel leetLevel) {
		this.leetLevel = leetLevel;
	}

	public EnumSet<UrlComponents> getUrlComponents() {
		return urlComponents;
	}

	public void setUrlComponents(EnumSet<UrlComponents> urlComponents) {
		this.urlComponents = urlComponents;
	}

	public short getLengthOfPassword() {
		return lengthOfPassword;
	}

	public void setLengthOfPassword(short lengthOfPassword) {
		this.lengthOfPassword = lengthOfPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getCharacters() {
		return characters;
	}

	public void setCharacters(CharacterSetSelection characters) {
		setCharacters(characters.getCharacterSet());
	}
	
	public void setCharacters(String characters) {
		this.characters = characters;
	}

	public String getPrefix() {
		return passwordPrefix;
	}

	public void setPrefix(String passwordPrefix) {
		this.passwordPrefix = passwordPrefix;
	}

	public String getSuffix() {
		return passwordSuffix;
	}

	public void setSuffix(String passwordSuffix) {
		this.passwordSuffix = passwordSuffix;
	}
}
