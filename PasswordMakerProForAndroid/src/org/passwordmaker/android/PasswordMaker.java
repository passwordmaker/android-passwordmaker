/*
 *  Copyright 2011 James Stapleton
 * 
 *  This file is part of PasswordMaker Pro For Android.
 *
 *  PasswordMaker Pro For Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  PasswordMaker Pro For Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PasswordMaker Pro For Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.passwordmaker.android;

import org.passwordmaker.android.LeetConverter.LeetLevel;

public class PasswordMaker {
	PwmProfile profile = new PwmProfile();
	
	public PasswordMaker() {}
	
	public PasswordMaker(PwmProfile profile) {
		this.profile = profile;
	}
	
	public void setProfile(PwmProfile selProfile) {
		profile = selProfile;
	}
	
	public PwmProfile getProfile() {
		return profile;
	}
	
	public boolean matchesPasswordHash(String masterPassword) {
		if ( ! profile.hasPasswordHash() ) {
			return true;
		}
		String testPassHash = generatePassword(profile.getPasswordSalt(), masterPassword);
		return testPassHash.equals(profile.getCurrentPasswordHash());
	}
	
	public String generatePassword(String inputText, String masterPassword) {
		final PwmHashAlgorithm hasher = profile.getHashAlgo();
		StringBuilder password = new StringBuilder();
		final String charSet = profile.getCharacters();
		final int maxLen = profile.getLengthOfPassword();
		int count = 0;
		final LeetLevel leetLevel = profile.getLeetLevel();
		String data = getData(inputText);
		if ( profile.getUseLeet().useBefore() ) {
			masterPassword = LeetConverter.convert(leetLevel, masterPassword);
			data = LeetConverter.convert(leetLevel, data);
		}
		
		while ( password.length() < maxLen ) {
			final String buildPass;
			if ( count == 0 ) {
				buildPass = hasher.hash(masterPassword, data, charSet);
			} else {
				buildPass = hasher.hash(masterPassword + "\n" + Integer.toString(count), data, charSet);
			}
			password.append(buildPass);
			count++;
		}
		String strPass = password.toString();
		if ( profile.getUseLeet().useAfter() ) {
			strPass = LeetConverter.convert(leetLevel, strPass);
		}
		
		if ( profile.getPrefix().length() > 0 ) {
			strPass = profile.getPrefix() + strPass;
		} 
		if ( profile.getSuffix().length() > 0 ) {
			int loc = maxLen - profile.getSuffix().length();
			if ( loc < strPass.length() ) {
				strPass = strPass.substring(0, loc) + profile.getSuffix();
			} else {
				strPass = strPass + profile.getSuffix();
			}
		}
		if ( strPass.length() > maxLen ) {
			strPass = strPass.substring(0, maxLen);
		}
		return strPass;
	}

	private String getData(String inputText) {
		String username = profile.getUsername();
		String modifier = profile.getModifier();
		StringBuilder buffer = new StringBuilder(inputText.length()
				+ username.length() + modifier.length() );
		buffer.append(inputText).append(username).append(modifier);
		return buffer.toString();
	}
}
