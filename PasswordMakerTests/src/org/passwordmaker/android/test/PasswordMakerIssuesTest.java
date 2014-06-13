package org.passwordmaker.android.test;

import junit.framework.TestCase;
import org.passwordmaker.android.*;

import java.util.EnumSet;

public class PasswordMakerIssuesTest extends TestCase {

    public void testIssue10() {
        PasswordMaker pwm = new PasswordMaker();
        pwm.getProfile().setHashAlgo(PwmHashAlgorithm.get(HashAlgo.MD5));
        pwm.getProfile().setLengthOfPassword((short) 8);
        pwm.getProfile().setUseLeet(LeetConverter.UseLeet.NotAtAll);
        pwm.getProfile().setCharacters(CharacterSetSelection.alphaNum);
        pwm.getProfile().setUrlComponents(EnumSet.of(PwmProfile.UrlComponents.Domain));
        final String masterPassword = "happy";
        assertEquals("HRdgNiyh", pwm.generatePassword("google.com", masterPassword));
        assertEquals("DsTpW36p", pwm.generatePassword("cnn.com", masterPassword));
        assertEquals("Fn4n23hm", pwm.generatePassword("google.co.uk", masterPassword));
        assertEquals("Fn4n23hm", pwm.generatePassword("www.google.co.uk", masterPassword));
        assertEquals("G78kh9hf", pwm.generatePassword("news.bbc.co.uk", masterPassword));
        assertEquals("BZmxeqtq", pwm.generatePassword("random.co.uk", masterPassword));
    }
}
