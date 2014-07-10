package org.passwordmaker;

import org.daveware.passwordmaker.*;

import java.security.Security;

import static org.passwordmaker.TestUtils.saToString;

/**
 * The purpose of this test is to make sure that the hash functions are available on the Android Emulator.
 *
 * The real unit tests for this functionality is in the passwordmaker-je-lib
 *
 */
public class PwmTest extends junit.framework.TestCase {
    static {
        PasswordMaker.setDefaultCryptoProvider("SC");
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public void performTest(AlgorithmType algorithmType, boolean useHMac, String expected) throws Exception {
        Account profile = new Account();
        profile.setCharacterSet(CharacterSets.ALPHANUMERIC);
        profile.setAlgorithm(algorithmType);
        profile.setHmac(useHMac);
        profile.setLength(8);
        profile.clearUrlComponents();
        profile.addUrlComponent(Account.UrlComponents.Domain);

        SecureCharArray masterPassword = new SecureCharArray("happy");

        PasswordMaker pwm = new PasswordMaker();
        assertEquals(expected, saToString(pwm.makePassword(masterPassword, profile, "google.com")));
    }

    public void testMD5() throws Exception {
        performTest(AlgorithmType.MD5, false, "HRdgNiyh");
    }
    public void testMD4() throws Exception {
        performTest(AlgorithmType.MD4, false, "HtzLxeLD");
    }
    public void testRIPEMD160() throws Exception {
        performTest(AlgorithmType.RIPEMD160, false, "joh9YCZc");
    }
    public void testSHA1() throws Exception {
        performTest(AlgorithmType.SHA1, false, "iEXyQtf6");
    }
    public void testSHA256() throws Exception {
        performTest(AlgorithmType.SHA256, false, "w8BStwWP");
    }
    public void testMD5HMac() throws Exception {
        performTest(AlgorithmType.MD5, true, "BdVB2Ye3");
    }
    public void testMD4HMac() throws Exception {
        performTest(AlgorithmType.MD4, true, "FYrXl6y9");
    }
    public void testRIPEMD160HMac() throws Exception {
        performTest(AlgorithmType.RIPEMD160, true, "IeMWw25Q");
    }
    public void testSHA1HMac() throws Exception {
        performTest(AlgorithmType.SHA1, true, "YqVH5OAk");
    }
    public void testSHA256HMac() throws Exception {
        performTest(AlgorithmType.SHA256, true, "Qljpvcsf");
    }
}
