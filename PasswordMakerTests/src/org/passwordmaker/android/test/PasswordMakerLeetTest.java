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

package org.passwordmaker.android.test;

import org.passwordmaker.android.CharacterSetSelection;

import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.PasswordMaker;
import org.passwordmaker.android.PwmProfile;
import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;

import junit.framework.TestCase;

public class PasswordMakerLeetTest  extends TestCase {
	
	private void _testLeet(HashAlgo hash, LeetLevel level, String expected) {
		PasswordMaker pwm = new PasswordMaker();
		PwmProfile profile = new PwmProfile("Hello");
		profile.setCharacters(CharacterSetSelection.alphaNum);
		profile.setHashAlgo(hash);
		profile.setLeetLevel(level);
		profile.setLengthOfPassword((short)8);
		profile.setUseLeet(UseLeet.AfterGeneratingPassword);
		pwm.setProfile(profile);
		String value = pwm.generatePassword("google.com", "pwmIsCool");
		assertEquals(expected, value);
	}
	
	public void testLeet1WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.One, "9r3mcjw1");
	}
	
	public void testLeet2WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.Two, "9r3mcjw1");
	}
	
	public void testLeet3WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.Three, "9r3mcjw1");
	}
	
	public void testLeet4WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.Four, "9r3mcjw1");
	}
	
	public void testLeet5WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.Five, "9|23mc7w");
	}
	
	public void testLeet6WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.Six, "9|2&mc,|");
	}	
	
	public void testLeet7WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.Seven, "9|2&^^[,");
	}
	
	public void testLeet8WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.Eight, "(,)|2&|\\");
	}
	
	public void testLeet9WithSha256() {
		_testLeet(HashAlgo.SHA_256, LeetLevel.Nine, "(,)|2&/\\");
	}
}
