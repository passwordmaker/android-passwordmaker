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

import org.passwordmaker.android.HashAlgo;
import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;
import org.passwordmaker.android.PasswordMaker;
import org.passwordmaker.android.PwmProfile;

import junit.framework.TestCase;

public class PasswordMakerMasterPasswordVerificationCodeTest extends TestCase {
	private PasswordMaker pwm = new PasswordMaker();

	public PwmProfile _setupPwm() {
		pwm = new PasswordMaker();
		PwmProfile profile = pwm.getProfile();
		profile.setCharacters("abcABC");
		profile.setHashAlgo(HashAlgo.HMAC_MD4);
		profile.setLengthOfPassword((short) 2);
		profile.setModifier("");
		profile.setPrefix("prefix");
		profile.setSuffix("suffix");
		profile.setUseLeet(UseLeet.NotAtAll);
		return profile;
	}

	public void testEmptyMasterPassword() {
		_setupPwm();
            assertEquals("gNV", pwm.generateVerificationCode(""));
	}

	public void testVeryShortMasterPassword() {
		_setupPwm();
		assertEquals("YJO", pwm.generateVerificationCode("h"));
	}

	public void testLongMasterPassword() {
		_setupPwm();
		assertEquals("RHd", pwm.generateVerificationCode("happybirthday"));
	}

    /** This unit test shows that configuring a password maker profile with the same settings that
     *  the {@link PasswordMaker#generateVerificationCode} uses will generate the same output.
     */
    public void testPwmWithSameProfileSettings() {
        pwm = new PasswordMaker();
        PwmProfile profile = pwm.getProfile();
        profile.setCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        profile.setHashAlgo(HashAlgo.SHA_256);
        profile.setLengthOfPassword((short) 3);
        profile.setModifier("");
        profile.setPrefix("");
        profile.setSuffix("");
        profile.setUseLeet(UseLeet.NotAtAll);
        assertEquals("KPA", pwm.generatePassword("happy", ""));
    }
	
	public void testProfileDoesntMatter() {
		_setupPwm();
		assertEquals("KPA", pwm.generateVerificationCode("happy"));
		pwm.getProfile().setCharacters("1234567890");
		assertEquals("KPA", pwm.generateVerificationCode("happy"));
		pwm.getProfile().setUseLeet(UseLeet.BeforeAndAfterGeneratingPassword);
		pwm.getProfile().setLeetLevel(LeetLevel.Nine);
		assertEquals("KPA", pwm.generateVerificationCode("happy"));
		pwm.getProfile().setModifier("modifier");
		assertEquals("KPA", pwm.generateVerificationCode("happy"));
		pwm.getProfile().setHashAlgo(HashAlgo.MD5);
		assertEquals("KPA", pwm.generateVerificationCode("happy"));
	}
}
