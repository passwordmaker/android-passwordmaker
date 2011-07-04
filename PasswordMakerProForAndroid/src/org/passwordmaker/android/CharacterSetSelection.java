package org.passwordmaker.android;

public enum CharacterSetSelection {
	alphaNumSym("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789`~!@#$%^&*()_-+={}|[]\\:\";'<>?,./"), 
	alphaNum("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"), 
	hex("0123456789abcdef"), 
	num("0123456789"), 
	alpha("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"), 
	sym("`~!@#$%^&*()_-+={}|[]\\:\";'<>?,./");
	
	private final String characterSet;
	
	private CharacterSetSelection(String characterSet) {
		this.characterSet = characterSet;
	}

	public String getCharacterSet() {
		return characterSet;
	}
}
