package org.passwordmaker.android.test;

import org.passwordmaker.android.CharacterSetSelection;
import org.passwordmaker.android.PasswordMaker;
import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;

import junit.framework.TestCase;

public class PasswordMakerMd5Test extends TestCase {
	public void testBasic() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLengthOfPassword((short)12);
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("U9HGvsEd0JfP", genPass);
	}
	
	public void testWithUsername () {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLengthOfPassword((short)12);
		pwm.getProfile().setUsername("james");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("CMlyOT11tXAi", genPass);
	}
	
	public void testWithUsernameAndModifier () {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLengthOfPassword((short)12);
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setModifier("aaa");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("CeqlaYYFF184", genPass);
	}
	
	public void testWithSuffix  () {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLengthOfPassword((short)12);
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setSuffix("sW");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("CMlyOT11tXsW", genPass);
	}
	
	public void testWithPrefix() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLengthOfPassword((short)12);
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setPrefix("r213");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("r213CMlyOT11", genPass);
	}
	
	public void testWithPrefixAndSuffix() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLengthOfPassword((short)12);
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setPrefix("r213");
		pwm.getProfile().setSuffix("sW");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("r213CMlyOTsW", genPass);
	}
	
	public void testWithPrefixAndSuffixComplete() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLengthOfPassword((short)12);
		pwm.getProfile().setUsername("james");
		pwm.getProfile().setPrefix("r213Ty2");
		pwm.getProfile().setSuffix("sWwww3");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("r213TysWwww3", genPass);
	}
	
	public void testMaxLen() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLengthOfPassword((short)8);
		pwm.getProfile().setUsername("james");
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("CMlyOT11", genPass);
		pwm.getProfile().setLengthOfPassword((short)2);
		genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("CM", genPass);
		pwm.getProfile().setLengthOfPassword((short)64);
		genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("CMlyOT11tXAis3JXsJcxiVBgxFgEcLycnQ0hDLfX5SCaD683GGR3M4upIxxZ0MYx", genPass);
		pwm.getProfile().setLengthOfPassword((short)1024); // something thats really really long ...
		genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("CMlyOT11tXAis3JXsJcxiVBgxFgEcLycnQ0hDLfX5SCaD683GGR3M4upIxxZ0MYxrbDPz" + 
				"gAJfOGM6N33h3ujXM9eG7963t7KVUIPwXhIuQX5POGPn3fTTaGFn24AlY0KQMQGDdbKiMhHsoC" + 
				"HSIXLMTvJcFGBpfQauuqd66fbclMyY7ftB9CkBd8YCMZt1KtWg8E48hEJUxazdG0ZuZLFzmEFVY" +
				"FE1YLu6MfNBwWQ4COd6QdrbCLhC82tcG2e0yNNYFqdZo8Cp8r2g0eaut63PBDCvadHLFxa8fJMjL" +
				"TQPGXD8Yw46U2HNIQdOZcrEp8TDvbQawKD6EvZ3Q2WKAPzCjhJe9eUpm0cX0yJLD08AbRBI96Ttrz" +
				"OHfMDLboO9jkI1gMcCAnYiZCy6Dewjsufu8QrUGGJMzCWCjXixb5dWsclFGH3nLuvRixvfHSVay12H" +
				"JXRkk3wxWFEwVu7zjayEvaoTfkbrdeyIBcC5TwJ4HEauLWTCQ6L69lGTnNBjsnpI5dN5OSG93MMFHJ3" +
				"OyKTdaF9viPrQ978RxBHgYOhmOG9380uGFVgad00QEIsSJj9rrBqmVLX8CTx64DGyH52c5YtRMyJq4tW" +
				"2gIAdGfynAuC7uV2jd90FXnZBOXFpfejzhL7H2m9BKRG2CHTaEJqsl9vejqPEOV5MLbQx5lBWy1G5HJ74" +
				"u1NWdUljrhsGDcvdT87bWJaoDCfmDmrM79C4yzBft6cBllDbsQdMdv4iF70rCVHTmwtEAjjzLbD2Rm" +
				"FpUq0MwOMHsMiAgF4YvTZuCp4fCEQy09S3EJcQUytTBQGNjbzEta1CJUugJ3UMejbCD8iK1a7wEprto" +
				"eIP6l9cn7FVdqO6SBI5B6noORq8WW9sCDIvkgYmFTETj1c7wZQY0fE2fP0eREMRA1gZ7Yiz8yXVE83j" +
				"7MYBL64D14fVQuH7E8GwP5j3pCzUiCpOropvAyuiEJi8Wd7u7Y1awCn0CMjnxeDa7YnhhTGcToFk1zS" +
				"XeJAFGNRSeKDirhCUAD5", genPass);
	}
	
	public void testLeetBeforeLevel3() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLeetLevel(LeetLevel.Three);
		pwm.getProfile().setUseLeet(UseLeet.BeforeGeneratingPassword);
		pwm.getProfile().setLengthOfPassword((short)12);
		pwm.getProfile().setCharacters(CharacterSetSelection.alphaNumSym);
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("IQ`iA[}E-/pB", genPass);
	}
	
	public void testLeetAfterLevel7() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLeetLevel(LeetLevel.Seven);
		pwm.getProfile().setUseLeet(UseLeet.AfterGeneratingPassword);
		pwm.getProfile().setLengthOfPassword((short)12);
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("(_)9#6\\/5&|)", genPass);
	}
	
	public void testLeetBothLevel9() {
		PasswordMaker pwm = new PasswordMaker();
		pwm.getProfile().setLeetLevel(LeetLevel.Nine);
		pwm.getProfile().setUseLeet(UseLeet.BeforeAndAfterGeneratingPassword);
		pwm.getProfile().setLengthOfPassword((short)12);
		String genPass = pwm.generatePassword("google.com", "hello");
		assertEquals("&'/|\\||>\\/(,", genPass);
	}
	
}
