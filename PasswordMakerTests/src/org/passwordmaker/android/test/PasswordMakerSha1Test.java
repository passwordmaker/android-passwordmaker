package org.passwordmaker.android.test;

import org.passwordmaker.android.CharacterSetSelection;
import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.PasswordMaker;
import org.passwordmaker.android.PwmHashAlgorithm;
import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;

import junit.framework.TestCase;

public class PasswordMakerSha1Test extends TestCase {
	private PasswordMaker getPWM() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setHashAlgo(PwmHashAlgorithm.get(HashAlgo.SHA_1));
		pwm.getProfile().setLengthOfPassword((short)12);
		return pwm;
	}
	
	public void testBasic() {
		PasswordMaker pwm = getPWM();
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("GFx2T9FVauS5", genPass);
	}
	
	public void testWithUsername () {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setUsername("james");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("D5XnIracRefA", genPass);
	}
	
	public void testWithUsernameAndModifier () {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setModifier("aaa");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("QDNVM3u3kXTP", genPass);
	}
	
	public void testWithSuffix  () {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setSuffix("sW");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("D5XnIracResW", genPass);
	}
	
	public void testWithPrefix() {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setPrefix("r213");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("r213D5XnIrac", genPass);
	}
	
	public void testWithPrefixAndSuffix() {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setPrefix("r213");
		pwm.getProfile().setSuffix("sW");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("r213D5XnIrsW", genPass);
	}
	
	public void testWithPrefixAndSuffixComplete() {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setPrefix("r213Ty2");
		pwm.getProfile().setSuffix("sWwww3");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("r213TysWwww3", genPass);
	}
	
	public void testMaxLen() {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setLengthOfPassword((short)8);
		pwm.getProfile().setUsername("james");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("D5XnIrac", genPass);
		pwm.getProfile().setLengthOfPassword((short)2);
		genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("D5", genPass);
		pwm.getProfile().setLengthOfPassword((short)64);
		genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("D5XnIracRefASbxbPJBDAdRdwedXgkmdr0IAV5Gq7jhoVhx9rKfaNDCO8GkoH1nJ", genPass);
		pwm.getProfile().setLengthOfPassword((short)1024); // something thats really really long ...
		genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("D5XnIracRefASbxbPJBDAdRdwedXgkmdr0IAV5Gq7jhoVhx9rKfaNDCO8GkoH1nJ0lvUu1jh4cHVbojCmbtdu0Bon" +
				"wvFq7g3m6aw5FwidHCFOQBempZ6V5AyCreNNnHDGAMyZtQQV4FPXTKy3NGrlALtkohGvkifexX9zkh6vPQocs618QgmFFh" +
				"hzE5JBiEUxgQjpjhcWrmOYRCOAFy0qWM3D2WTDNLAxhcYUxwQU3mUG9RMJgTfdTjqVJmqtO8T40n9C8UnkgjKEwib6gbQe" +
				"mR0nkkXvaX0jAEXeOePsDvtD3XW3KvhD3LsqHdqW90FAQKTHBDfwZGSq9H3FhPwNAjDUZcueAagahlJFw3ZtYHq8OODMMx" +
				"r4DYzjLXWchCu1C4iYc1GuU9guBgbmAfs0Lu3SaxlzCt0mzsvpLNXYj7vzHNaIrO7csj2H30sjjCR3ftBRqc7786UB5IkM" +
				"Q31WdaV99NmlzZadfUdKiMlqd2eKnIipN8oaL70J9rUuLquBK1QI0xl5eRRzmoTD7bEb1QBQsmvHs5s5DGBtD6VtUofEq4" +
				"LTyaLnj6aAr2zsJl9O0ttgDjCDw5mK9xsCgVLhKPZLOtHPs1cZaSeeWMQ6MpStbQBPP3gL16nzkPTK0mcDPVq3xRaOmeif" +
				"7VpBYeR2UdORrJU9Y4Lh20DL4IkdKO2OL6NFaa5M31yfzEJVxMmrJpKmzHq8WAZBOsC2AdInWD0KGA3PwKM3Jj0JTua2wl" +
				"SUJJC8AAYKe6JQen0xAgHtLl3qYmK9VUyefkOIbjxRtSvl19oPN4lE0QzH6CX1kUnhsC423ajXNH9eJ7EHUeTQioduT8WF" +
				"i1KppHJib33t8eQvaNBBAkpE2WU6w4Q9AQQhihdAiwRuFpN6YIKmOkEtKY1XdoHcoeVFXIed6yXUKNWA393cI5L7Fu6PZp" +
				"33Tx8DSxRAQV3kUJTdbkwT9YWuhD7ixAEA9a7I8DFxnZWY6afO0dZZwTWvKS08YsT7Bsz0WUeWkMIm53fDudahrl7", genPass);
	}
	
	public void testLeetBeforeLevel3() {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setLeetLevel(LeetLevel.Three);
		pwm.getProfile().setUseLeet(UseLeet.BeforeGeneratingPassword);
		pwm.getProfile().setCharacters(CharacterSetSelection.alphaNumSym);
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("B!u<qc(:!0NJ", genPass);
	}
	
	public void testLeetAfterLevel7() {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setLeetLevel(LeetLevel.Seven);
		pwm.getProfile().setUseLeet(UseLeet.AfterGeneratingPassword);
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("6|=><279|=\\/", genPass);
	}
	
	public void testLeetBothLevel9() {
		PasswordMaker pwm = getPWM();
		pwm.getProfile().setLeetLevel(LeetLevel.Nine);
		pwm.getProfile().setUseLeet(UseLeet.BeforeAndAfterGeneratingPassword);
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("!\\/(,)!\\^/!/", genPass);
	}
	
}
