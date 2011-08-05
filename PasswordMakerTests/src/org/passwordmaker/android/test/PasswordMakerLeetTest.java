package org.passwordmaker.android.test;

import org.passwordmaker.android.CharacterSetSelection;

import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.PasswordMaker;
import org.passwordmaker.android.PwmProfile;
import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;

import com.tasermonkeys.google.json.Gson;

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
	
	public void testFuckHeads() {
		PwmProfile prof = new PwmProfile("FUCK");
		Gson gson = new Gson();
		String fuck =  gson.toJson(prof);
		prof = gson.fromJson(fuck, PwmProfile.class);
		
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
