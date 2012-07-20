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

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.PwmProfile.UrlComponents;

public class PasswordMaker {
	private static Pattern urlRegex = Pattern.compile("([^:\\/\\/]*:\\/\\/)?([^:\\/]*)([^#]*)");
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
	
	public String getModifiedInputText(String inputText) {
		EnumSet<UrlComponents> uriComponents = profile.getUrlComponents();
		if (uriComponents.isEmpty())
			return inputText;
		// var temp = location.href.match("([^://]*://)([^/]*)(.*)");
		Matcher matcher = urlRegex.matcher(inputText);
		if (!matcher.matches())
			return inputText;
		String protocol = matcher.group(1);
		String domainText = matcher.group(2);
		String portPath = matcher.group(3);
		if ( protocol == null ) protocol = "";
		if ( domainText == null ) domainText = "";
		if ( portPath == null ) portPath = "";
		
		StringBuilder retVal = new StringBuilder(inputText.length());
		if (uriComponents.contains(UrlComponents.Protocol)
				&& protocol.length() > 0) {
			retVal.append(protocol);
		}
		if (domainText != null) {
			final String subDomain;
			int dnDot = domainText.lastIndexOf('.');
			dnDot = domainText.lastIndexOf('.', dnDot - 1);
			if (dnDot != -1) {
				subDomain = domainText.substring(0, dnDot);
				domainText = domainText.substring(dnDot + 1);
			} else {
				subDomain = "";
			}
			final boolean hasSubDomain = uriComponents
					.contains(UrlComponents.Subdomain) && dnDot != -1;
			if (hasSubDomain) {
				retVal.append(subDomain);
			}
			if (uriComponents.contains(UrlComponents.Domain)) {
				if (hasSubDomain)
					retVal.append('.');
				retVal.append(domainText);
			}
		}
		if (uriComponents.contains(UrlComponents.PortPathAnchorQuery)
				&& portPath.length() > 0) {
			retVal.append(portPath);
		}
		return retVal.toString();
		
	}
	
	public String generatePassword(String inputText, String masterPassword) {
		final PwmHashAlgorithm hasher = profile.getHashAlgo();
		StringBuilder password = new StringBuilder();
		final String charSet = profile.getCharacters();
		final int maxLen = profile.getLengthOfPassword();
		int count = 0;
		final LeetLevel leetLevel = profile.getLeetLevel();
		String data = getData(getModifiedInputText(inputText));
		
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

	public String generateVerificationCode(String masterPassword) {
		final PwmHashAlgorithm hasher = PwmHashAlgorithm.get(HashAlgo.SHA_256);
		StringBuilder password = new StringBuilder();
		final String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		final int maxLen = 3;
		int count = 0;
				
		while ( password.length() < maxLen ) {
			final String buildPass;
			if ( count == 0 ) {
				buildPass = hasher.hash(masterPassword, "", charSet);
			} else {
				buildPass = hasher.hash(masterPassword + "\n" + Integer.toString(count), "", charSet);
			}
			password.append(buildPass);
			count++;
		}
		String strPass = password.toString();
		
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
